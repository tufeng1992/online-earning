package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.*;
import com.powerboot.dao.MemberInfoDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.MemberInfoDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.PayEnums.RechargeCheckEnum;
import com.powerboot.enums.PaymentServiceEnum;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.LoanDetailRequest;
import com.powerboot.response.pay.RechargeAmountDoc;
import com.powerboot.response.pay.RechargeAmountInfo;
import com.powerboot.response.pay.WalletResult;
import com.powerboot.service.*;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.SpringContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;


/**
 * 支付结果
 */

@RestController
@RequestMapping("/pay")
@Api(tags = "支付")
public class PayController extends BaseController {
    @Autowired
    private PayService payService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private WeGamePayService weGamePayService;
    @Autowired
    private UserService userService;

    @Autowired
    private MemberInfoDao memberInfoDao;

    private static StringBuilder rechargeDoc = new StringBuilder();
    static {
        rechargeDoc.append("");
    }


    //风控规则
    public BaseResponse checkRule(LoanDetailRequest param,UserDO userDO) {
        //封盘开关 0-关 1-开
        Integer goPaySwitch = RedisUtils.getInteger(DictConsts.GO_PAY_SWITCH);
        if (goPaySwitch == null || goPaySwitch.equals(PayEnums.GoPaySwitchEnum.OPEN.getCode())) {
            //封盘时间
            String goPayDate = RedisUtils.getString(DictConsts.GO_PAY_DATE);
            if (StringUtils.isBlank(goPayDate) ||
                    DateUtils.parseDate(goPayDate, DateUtils.DATE_TIME_PATTERN).compareTo(DateUtils.now()) < 0) {
                return BaseResponse.fail(RedisUtils.getString(DictConsts.GO_PAY_TIP));
            }
        }
        //充值总开关 0-关 1-开
        Integer paySwitch = RedisUtils.getInteger(DictConsts.PAY_SWITCH);
        if (paySwitch == null || PayEnums.PaySwitchEnum.CLOSE.getCode().equals(paySwitch)) {
            return BaseResponse.fail(RedisUtils.getValue(DictConsts.PAY_CLOSE_CONTENT, String.class));
        }

        //个人充值开关 0-关 1-开
        if (RechargeCheckEnum.CLOSE.getCode().equals(userDO.getRechargeCheck())) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.PAY_CLOSE_USER_CONTENT));
        }
        //充值金额列表
        String rechargeAmountListStr = RedisUtils.getString(DictConsts.RECHARGE_AMOUNT_LIST);
        String[] rechargeAmountArray = rechargeAmountListStr.split("-");
        String minAmount = rechargeAmountArray[0];
        if (param.getType() == null || BigDecimal.ZERO.compareTo(param.getPayAmount()) >= 0) {
            return BaseResponse.fail(String.format(I18nEnum.RECHARGE_LOW_FAIL.getMsg(), minAmount));
        }

        if (param.getType() == 1 && new BigDecimal(minAmount).compareTo(param.getPayAmount()) > 0) {
            return BaseResponse.fail(String.format(I18nEnum.RECHARGE_LOW_FAIL.getMsg(), minAmount));
        }
        //校验充值规则
        if (param.getType() == 1) {
            //校验充值次数上限
            Integer rechargeCount = RedisUtils.getValue(CacheConsts.getTodayRechargeKey(userDO.getId()), Integer.class);
            Integer sysRechargeCount = RedisUtils.getValue(DictConsts.TODAY_MAX_RECHARGE_COUNT, Integer.class);
            if (rechargeCount != null && rechargeCount.compareTo(sysRechargeCount) >= 0) {
                return BaseResponse.fail(String.format(I18nEnum.RECHARGE_LIMIT_DAY.getMsg(), sysRechargeCount));
            }
            //校验充值金额限制
            /**
             * recharge 3000 and above, need to invite 3 users to register,
             * recharge 8000 and above, need to invite 10 users to register,
             * recharge 20,000 and above, need to invite 20 users to register,
             *
             */

        }
        return BaseResponse.success();
    }

    @ApiOperation(value = "创建支付订单:充值,购买VIP")
    @PostMapping("/create")
    public BaseResponse<PayDO> createOrder(@RequestBody @Valid LoanDetailRequest param) {
        UserDO userDO = userService.get(getUserId(param));
        if (userDO == null) {
            return BaseResponse.fail(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
        }

        //充值风控校验
        BaseResponse checkResult = checkRule(param,userDO);
        if (checkResult.isFail()){
            return checkResult;
        }
        if (param.getType().equals(1)) {
            if (StringUtils.isEmpty(userDO.getAccountNumber())) {
                return BaseResponse.fail(I18nEnum.PAY_BIND_CARD_FAIL.getCode(), I18nEnum.PAY_BIND_CARD_FAIL.getMsg());
            }
            if (StringUtils.isEmpty(userDO.getName())) {
                return BaseResponse.fail(I18nEnum.PAY_NAME_FAIL.getCode(), I18nEnum.PAY_NAME_FAIL.getMsg());
            }
        }
        if (!param.getType().equals(1)) {
            int childFirstRechargedCount = 9999;
            String createVipChildLimitSwitch = RedisUtils.getString(DictConsts.CREATE_VIP_CHILD_LIMIT_SWITCH);
            if (StringUtils.isNotBlank(createVipChildLimitSwitch) && "true".equalsIgnoreCase(createVipChildLimitSwitch)) {
                List<UserDO> userDOList = userService.getUserByParentId(userDO.getId());
                //开关打开的情况下限制数量
                childFirstRechargedCount = userDOList.size();
            }
            BigDecimal VIPAmount;
            MemberInfoDO m = new MemberInfoDO();
            m.setType(param.getType());
            MemberInfoDO memberInfoDO = memberInfoDao.selectOne(m);
            if (memberInfoDO == null) {
                return BaseResponse.fail(I18nEnum.AMOUNT_FAIL.getMsg());
            }
            if (childFirstRechargedCount < memberInfoDO.getUpLimit()) {
                return BaseResponse.fail(I18nEnum.VIP_UPDATE_COUNT_FAIL.getMsg());
            }
            VIPAmount = memberInfoDO.getAmount();
            if (VIPAmount == null) {
                return BaseResponse.fail(I18nEnum.AMOUNT_FAIL.getMsg());
            }
            param.setPayAmount(VIPAmount);
        }
        param.setUserId(getUserId(param));
        return payService.createOrder(param, userDO);
    }

    @ApiOperation(value = "创建支付订单:购买VIP")
    @PostMapping("/createForBalance")
    public BaseResponse<PayDO> createOrderForBalance(@RequestBody @Valid LoanDetailRequest param) {
        UserDO userDO = userService.get(getUserId(param));
        if (userDO == null) {
            return BaseResponse.fail(I18nEnum.LOGIN_TIMEOUT_FAIL.getMsg());
        }

        //充值风控校验
        BaseResponse checkResult = checkRule(param,userDO);
        if (checkResult.isFail()){
            return checkResult;
        }

        if (!param.getType().equals(1)) {
            int childFirstRechargedCount = 9999;
            String createVipChildLimitSwitch = RedisUtils.getString(DictConsts.CREATE_VIP_CHILD_LIMIT_SWITCH);
            if (StringUtils.isNotBlank(createVipChildLimitSwitch) && "true".equalsIgnoreCase(createVipChildLimitSwitch)) {
                List<UserDO> userDOList = userService.getUserByParentId(userDO.getId());
                //开关打开的情况下限制数量
                childFirstRechargedCount = userDOList.size();
            }
            BigDecimal VIPAmount;
            if (param.getType().equals(2)) {
                if (childFirstRechargedCount < 5) {
                    return BaseResponse.fail(I18nEnum.VIP_UPDATE_COUNT_FAIL.getMsg());
                }
                VIPAmount = RedisUtils.getValue(DictAccount.VIP2_CHARGE, BigDecimal.class);
            } else if (param.getType().equals(3)) {
                if (childFirstRechargedCount < 10) {
                    return BaseResponse.fail(I18nEnum.VIP_UPDATE_COUNT_FAIL.getMsg());
                }
                VIPAmount = RedisUtils.getValue(DictAccount.VIP3_CHARGE, BigDecimal.class);
            } else if (param.getType().equals(4)) {
                if (childFirstRechargedCount < 15) {
                    return BaseResponse.fail(I18nEnum.VIP_UPDATE_COUNT_FAIL.getMsg());
                }
                VIPAmount = RedisUtils.getValue(DictAccount.VIP4_CHARGE, BigDecimal.class);
            } else if (param.getType().equals(5)) {
                if (childFirstRechargedCount < 20) {
                    return BaseResponse.fail(I18nEnum.VIP_UPDATE_COUNT_FAIL.getMsg());
                }
                VIPAmount = RedisUtils.getValue(DictAccount.VIP5_CHARGE, BigDecimal.class);
            } else {
                return BaseResponse.fail(I18nEnum.AMOUNT_FAIL.getMsg());
            }
            if (VIPAmount == null) {
                return BaseResponse.fail(I18nEnum.AMOUNT_FAIL.getMsg());
            }
            param.setPayAmount(VIPAmount);
        } else {
            return BaseResponse.fail(I18nEnum.SYSTEM_FAIL.getMsg());
        }
        if (userDO.getBalance().compareTo(param.getPayAmount()) < 0) {
            return BaseResponse.fail(I18nEnum.CREATE_ORDER_BALANCE_FAIL.getMsg());
        }
        param.setUserId(getUserId(param));
        return payService.createOrderForBalance(param, userDO);
    }

    @ApiOperation("获取 充值 记录")
    @PostMapping("/recharge")
    public BaseResponse<List<PayDO>> getRecharge(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        return BaseResponse.success(payService.getRechargeByUserId(userId));
    }

    @ApiOperation("获取 充值 记录")
    @PostMapping("/balance")
    public BaseResponse<List<BalanceDO>> getBalance(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        return BaseResponse.success(balanceService.listByTypeAndUserId(userId, BalanceTypeEnum.G.getCode()));
    }

    @ApiOperation("获取 提现 记录")
    @PostMapping("/withdraw")
    public BaseResponse<List<BalanceDO>> getWithdraw(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        return BaseResponse.success(balanceService.listByTypeAndUserId(userId, BalanceTypeEnum.G.getCode()));
    }

    @ApiOperation(value = "同步订单状态")
    @PostMapping("/order/{orderNo}")
    public BaseResponse<PayDO> getPay(@PathVariable("orderNo") String orderNo) {
        return payService.getByOrderNo(orderNo);
    }

    @ApiOperation(value = "获取支付状态")
    @PostMapping("/status/{orderNo}")
    public BaseResponse<Integer> getPayState(@PathVariable("orderNo") String orderNo) {
        return payService.getStatusByOrderNo(orderNo);
    }

    @ApiOperation(value = "确认支付", notes = "需要:razorpay_payment_id,razorpay_order_id,razorpay_signature")
    @PostMapping("/back/status")
    public BaseResponse<PayDO> handelOrderStatus(@RequestBody Map<String, Object> requestJson) {
        return payService.handelOrderStatus(requestJson);
    }

    @ApiOperation("wegame订单查询")
    @GetMapping("/getWegame/{orderNo}")
    public BaseResponse getThirdOrder(@PathVariable("orderNo") String orderNo) {
        return BaseResponse.success(weGamePayService.getPayoutOrder(orderNo));
    }

    @ApiOperation("充值界面文案")
    @GetMapping("/recharge/doc")
    public BaseResponse getRechargeDoc() {
        return BaseResponse.success(rechargeDoc.toString());
    }

    @PostMapping("/rechargeSuccess")
    public BaseResponse getThirdOrder(@RequestBody Map<String, Object> requestJson) {
        if (MapUtils.isEmpty(requestJson)){
            return BaseResponse.fail(I18nEnum.SYSTEM_FAIL.getMsg());
        }
        String orderNo = requestJson.get("orderNo").toString();
        String checkoutRequestID = requestJson.get("checkoutRequestID").toString();
        PayDO payDO = payService.getOrderNo(orderNo);
        payDO.setThirdNo(checkoutRequestID);
        payService.updatePay(payDO);
        return BaseResponse.success();
    }

    @ApiOperation("充值界面文案")
    @GetMapping("/rechargeInfo")
    public BaseResponse<List<RechargeAmountInfo>> getRechargeInfo() {
        List<RechargeAmountInfo> rechargeAmountInfoList = new ArrayList<>();

        return BaseResponse.success(rechargeAmountInfoList);
    }


    @ApiOperation("充值文案")
    @GetMapping("/rechargeAmountDoc")
    public BaseResponse<RechargeAmountDoc> getRechargeAmount() {
        RechargeAmountDoc rechargeAmountDoc = new RechargeAmountDoc();
        String rechargeAmountListStr = RedisUtils.getString(DictConsts.RECHARGE_AMOUNT_LIST);
        String[] rechargeAmountArray = rechargeAmountListStr.split("-");
        String minAmount = rechargeAmountArray[0];
        List<Integer> rechargeAmount = new ArrayList<>();
        List<String> rechargeAmountText = new ArrayList<>();
        for(String amount:rechargeAmountArray){
            rechargeAmount.add(Integer.parseInt(amount));
            rechargeAmountText.add(amount);
        }
        String rechargeInputText = I18nEnum.RECHARGE_INPUT_TEXT.getMsg();
        rechargeAmountDoc.setRechargeAmount(rechargeAmount);
        rechargeAmountDoc.setRechargeAmountText(rechargeAmountText);
        rechargeAmountDoc.setRechargeInputText(String.format(rechargeInputText, minAmount));
        rechargeAmountDoc.setRechargeInputTextColor(RedisUtils.getString(DictConsts.RECHARGE_AMOUNT_TEXT_COLOR));
        rechargeAmountDoc.setMinimumRechargeAmount(new BigDecimal(minAmount));
        rechargeAmountDoc.setTips(RedisUtils.getString(DictConsts.RECHARGE_AMOUNT_TIPS));
        return BaseResponse.success(rechargeAmountDoc);
    }

}
