package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.StringCommonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.consts.CacheConsts;
import com.powerboot.consts.DictAccount;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.TipConsts;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.WithdrawalRecordDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.PayEnums.SysWithdrawalCheckEnum;
import com.powerboot.enums.PayEnums.WithdrawalCheckEnum;
import com.powerboot.enums.PaymentServiceEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.request.BankRequest;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.order.ApplyRequest;
import com.powerboot.request.payment.CreatePayOutOrder;
import com.powerboot.request.user.RenameRequest;
import com.powerboot.request.user.WithdrawRequest;
import com.powerboot.response.BuyMemberInfoResponse;
import com.powerboot.response.InvitationResponse;
import com.powerboot.response.MemberInfoDescDto;
import com.powerboot.response.MemberInfoDto;
import com.powerboot.response.MissionResponse;
import com.powerboot.response.NewMissionResponse;
import com.powerboot.response.PayVO;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.response.pay.WithdrawAmountDoc;
import com.powerboot.service.BalanceService;
import com.powerboot.service.BlackUserService;
import com.powerboot.service.EhcacheService;
import com.powerboot.service.InviteLogService;
import com.powerboot.service.PayService;
import com.powerboot.service.PaymentService;
import com.powerboot.service.UserService;
import com.powerboot.service.WithdrawalRecordService;
import com.powerboot.utils.DateUtils;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.SpringContextUtils;
import com.powerboot.utils.StringRandom;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户服务")
public class UserController extends BaseController {

    @Autowired
    WithdrawalRecordService withdrawalRecordService;
    @Autowired
    PayService payService;
    @Autowired
    BalanceService balanceService;
    @Autowired
    EhcacheService ehcacheService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlackUserService blackUserService;
    @Autowired
    private InviteLogService inviteLogService;

    @PostMapping("/memberInfo")
    @ApiOperation(value = "购买会员卡页面")
    public BaseResponse<List<MemberInfoDto>> memberInfo(@RequestBody @Valid BaseRequest request) {
        BaseResponse<List<MemberInfoDto>> response = BaseResponse.success();
        Long userId = getUserId(request);
        List<MemberInfoDto> list = new ArrayList<>();

        //vip1
        MemberInfoDto vip1 = new MemberInfoDto();
        vip1.setLimitAmount(
            new BigDecimal(RedisUtils.getValue(DictConsts.VIP1_SINGLE_MAX_WITHDRAW_QUOTA, String.class)));
        vip1.setWithdrawTimes(
            Integer.valueOf(RedisUtils.getValue(DictConsts.VIP1_TODAY_MAX_WITHDRAW_COUNT, String.class)));

        HashMap<Integer, List<Integer>> vipInfo = ehcacheService.getVipInfo();
        List<Integer> vip1List = vipInfo.get(1);
        vip1.setFeeStr(vip1List.get(0) + "%/" + vip1List.get(1) + "%/" + vip1List.get(2) + "%");
        vip1.setAmount(BigDecimal.ZERO);
        vip1.setOrderTimes(vip1List.get(3));
        vip1.setVip("VIP1");
        vip1.setPicUrl("http://aaa.com/image/vip1.png");
        list.add(vip1);

        //vip2
        MemberInfoDto vip2 = new MemberInfoDto();
        vip2.setLimitAmount(
            new BigDecimal(RedisUtils.getValue(DictConsts.VIP2_SINGLE_MAX_WITHDRAW_QUOTA, String.class)));
        vip2.setWithdrawTimes(
            Integer.valueOf(RedisUtils.getValue(DictConsts.VIP2_TODAY_MAX_WITHDRAW_COUNT, String.class)));

        List<Integer> vip2List = vipInfo.get(2);
        vip2.setFeeStr(vip2List.get(0) + "%/" + vip2List.get(1) + "%/" + vip2List.get(2) + "%");
        vip2.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP2_CHARGE, String.class)));
        vip2.setOrderTimes(vip2List.get(3));
        vip2.setType(2);
        vip2.setVip("VIP2");
        vip2.setPicUrl("http://aaa.com/image/vip2.png");
        vip2.setVipDesc("Prerequisites for unlocking the LV2 task area");
        list.add(vip2);

        //vip3
        MemberInfoDto vip3 = new MemberInfoDto();
        vip3.setLimitAmount(
            new BigDecimal(RedisUtils.getValue(DictConsts.VIP3_SINGLE_MAX_WITHDRAW_QUOTA, String.class)));
        vip3.setWithdrawTimes(
            Integer.valueOf(RedisUtils.getValue(DictConsts.VIP3_TODAY_MAX_WITHDRAW_COUNT, String.class)));

        List<Integer> vip3List = vipInfo.get(3);
        vip3.setFeeStr(vip3List.get(0) + "%/" + vip3List.get(1) + "%/" + vip3List.get(2) + "%");
        vip3.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP3_CHARGE, String.class)));
        vip3.setOrderTimes(vip3List.get(3));
        vip3.setType(3);
        vip3.setVip("VIP3");
        vip3.setPicUrl("http://aaa.com/image/vip3.png");
        vip3.setVipDesc("Prerequisites for unlocking the LV3 task area");
        list.add(vip3);

        //vip4
        MemberInfoDto vip4 = new MemberInfoDto();
        vip4.setLimitAmount(
            new BigDecimal(RedisUtils.getValue(DictConsts.VIP4_SINGLE_MAX_WITHDRAW_QUOTA, String.class)));
        vip4.setWithdrawTimes(
            Integer.valueOf(RedisUtils.getValue(DictConsts.VIP4_TODAY_MAX_WITHDRAW_COUNT, String.class)));

        //vip4除了购买金额，其他与vip3保持一致
        List<Integer> vip4List = vipInfo.get(4);
        vip4.setFeeStr(vip4List.get(0) + "%/" + vip4List.get(1) + "%/" + vip4List.get(2) + "%");
        vip4.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP4_CHARGE, String.class)));
        vip4.setOrderTimes(vip4List.get(3));
        vip4.setType(4);
        vip4.setVip("VIP4");
        vip4.setPicUrl("http://aaa.com/image/vip4.png");
        vip4.setVipDesc("Prerequisites for unlocking the LV4 task area");
        list.add(vip4);

        response.setResultData(list);
        return response;
    }

    @PostMapping("/memberInfoNew")
    @ApiOperation(value = "购买会员卡页面-新")
    public BuyMemberInfoResponse<List<MemberInfoDescDto>> memberInfoNew(@RequestBody @Valid BaseRequest request) {
        BuyMemberInfoResponse<List<MemberInfoDescDto>> response = new BuyMemberInfoResponse();
        List<MemberInfoDescDto> list = new ArrayList<>();
        response.setTitle("Players will be LV1 members by default after registering an account");
        //vip1
        MemberInfoDescDto vip1 = new MemberInfoDescDto();
        vip1.setLimitAmount(
            "Each withdraw limit is " + RedisUtils.getValue(DictConsts.VIP1_SINGLE_MAX_WITHDRAW_QUOTA, String.class));
        vip1.setWithdrawTimes(
            "Withdraw cash " + RedisUtils.getValue(DictConsts.VIP1_TODAY_MAX_WITHDRAW_COUNT, String.class)
                + " times a day");

        HashMap<Integer, List<Integer>> vipInfo = ehcacheService.getVipInfo();
        List<Integer> vip1List = vipInfo.get(1);
        vip1.setFeeStr(
            "The promotion rebate is " + vip1List.get(0) + "%/" + vip1List.get(1) + "%/" + vip1List.get(2) + "%");
        vip1.setAmount(BigDecimal.ZERO);
        vip1.setOrderTimes("The number of orders can be swiped is" + vip1List.get(3));
        vip1.setVip("VIP1");
        vip1.setPicUrl("http://aaa.com/image/vip1.png");
        list.add(vip1);

        //vip2
        MemberInfoDescDto vip2 = new MemberInfoDescDto();
        vip2.setLimitAmount(
            "Each withdraw limit is " + RedisUtils.getValue(DictConsts.VIP2_SINGLE_MAX_WITHDRAW_QUOTA, String.class));
        vip2.setWithdrawTimes(
            "Withdraw cash " + RedisUtils.getValue(DictConsts.VIP2_TODAY_MAX_WITHDRAW_COUNT, String.class)
                + " times a day");

        List<Integer> vip2List = vipInfo.get(2);
        vip2.setFeeStr(
            "The promotion rebate is " + vip2List.get(0) + "%/" + vip2List.get(1) + "%/" + vip2List.get(2) + "%");
        vip2.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP2_CHARGE, String.class)));
        vip2.setOrderTimes("The number of orders can be swiped is" + vip2List.get(3));
        vip2.setType(2);
        vip2.setVip("VIP2");
        vip2.setPicUrl("http://aaa.com/image/vip2.png");
        vip2.setVipDesc("Prerequisites for unlocking the LV2 task area");
        list.add(vip2);

        //vip3
        MemberInfoDescDto vip3 = new MemberInfoDescDto();
        vip3.setLimitAmount(
            "Each withdraw limit is " + RedisUtils.getValue(DictConsts.VIP3_SINGLE_MAX_WITHDRAW_QUOTA, String.class));
        vip3.setWithdrawTimes(
            "Withdraw cash " + RedisUtils.getValue(DictConsts.VIP3_TODAY_MAX_WITHDRAW_COUNT, String.class)
                + " times a day");

        List<Integer> vip3List = vipInfo.get(3);
        vip3.setFeeStr(
            "The promotion rebate is " + vip3List.get(0) + "%/" + vip3List.get(1) + "%/" + vip3List.get(2) + "%");
        vip3.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP3_CHARGE, String.class)));
        vip3.setOrderTimes("The number of orders can be swiped is" + vip3List.get(3));
        vip3.setType(3);
        vip3.setVip("VIP3");
        vip3.setPicUrl("http://aaa.com/image/vip3.png");
        vip3.setVipDesc("Prerequisites for unlocking the LV3 task area");
        list.add(vip3);

        //vip4
        MemberInfoDescDto vip4 = new MemberInfoDescDto();
        vip4.setLimitAmount(
            "Each withdraw limit is " + RedisUtils.getValue(DictConsts.VIP4_SINGLE_MAX_WITHDRAW_QUOTA, String.class));
        vip4.setWithdrawTimes(
            "Withdraw cash " + RedisUtils.getValue(DictConsts.VIP4_TODAY_MAX_WITHDRAW_COUNT, String.class)
                + " times a day");

        //vip4除了购买金额，其他与vip3保持一致
        List<Integer> vip4List = vipInfo.get(4);
        vip4.setFeeStr(
            "The promotion rebate is " + vip4List.get(0) + "%/" + vip4List.get(1) + "%/" + vip4List.get(2) + "%");
        vip4.setAmount(new BigDecimal(RedisUtils.getValue(DictAccount.VIP4_CHARGE, String.class)));
        vip4.setOrderTimes("The number of orders can be swiped is" + vip4List.get(3));
        vip4.setType(4);
        vip4.setVip("VIP4");
        vip4.setPicUrl("http://aaa.com/image/vip4.png");
        vip4.setVipDesc("Prerequisites for unlocking the LV4 task area");
        list.add(vip4);

        response.setSuccess(true);
        response.setResultData(list);
        return response;
    }


    @PostMapping("/rename")
    @ApiOperation(value = "修改昵称")
    public BaseResponse rename(@RequestBody @Valid RenameRequest request) {
        Long userId = getUserId(request);
        UserDO userDO = userService.get(userId);

        userDO.setNikeName(request.getNikeName());
        return userService.updateByIdAndVersion(userDO) > 0 ? BaseResponse.success() : BaseResponse.fail("modify fall");
    }

    @PostMapping("/invitation")
    @ApiOperation(value = "邀请界面")
    public BaseResponse<InvitationResponse> invitation(@RequestBody @Valid BaseRequest request) {
        logger.info("param=====>{}", request);
        Long userId = getUserId(request);
        UserDO userDO = userService.get(userId);
        String urlKey = String.format(CacheConsts.INVITATION_PRE_URL, request.getAppId());
        String preUrl = RedisUtils.getValue(urlKey, String.class);
        InvitationResponse response = new InvitationResponse();
        response.setReferralCode(userDO.getReferralCode());
        response.setInvitationPreUrl(preUrl);
        if (userDO.getMemberLevel() == 1) {
            response.setLevelRatio(RedisUtils.getValue(DictAccount.VIP1, String.class));
        } else if (userDO.getMemberLevel() == 2) {
            response.setLevelRatio(RedisUtils.getValue(DictAccount.VIP2, String.class));
        } else if (userDO.getMemberLevel() == 3) {
            response.setLevelRatio(RedisUtils.getValue(DictAccount.VIP3, String.class));
        }
        return BaseResponse.success(response);
    }

    @PostMapping("/get")
    @ApiOperation(value = "获取当前用户基础信息")
    public BaseResponse<UserDO> getCurrentUser(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        UserDO userDO = userService.get(userId);
        return BaseResponse.success(userDO);
    }

    @ApiOperation("绑卡")
    @PostMapping("/bank")
    @Transactional
    public BaseResponse<Boolean> bank(@RequestBody @Valid BankRequest request) {
        Long userId = this.getUserId(request);
        if (userId == null) {
            return BaseResponse.fail(TipConsts.NO_LOGIN);
        }
        //风控校验
        blackUserService.blackCheck(request.getMobile(), request.getName(), request.getEmail(), userId);
        UserDO user = userService.get(userId);
        if (user == null) {
            return BaseResponse.fail(TipConsts.NO_LOGIN);
        }
        user.setFirstName(StringUtils.trim(request.getFirstName()));
        user.setName(StringUtils.trim(request.getName()));
        user.setLastName(StringUtils.trim(request.getLastName()));
        String phone = StringUtils.replace(request.getMobile(), " ", "");
        //补全手机号
        if (phone != null && phone.length() == 10 && "0".equals(phone.subSequence(0, 1))) {
            phone = MobileUtil.NIGERIA_MOBILE_PREFIX + phone.substring(1, phone.length());
        } else if (phone != null && phone.length() == 10) {
            phone = MobileUtil.NIGERIA_MOBILE_PREFIX + phone;
        }

        if (MobileUtil.isNigeriaMobile(phone)) {
            user.setAccountPhone(phone);
        } else {
            return BaseResponse.fail("nrecognized,please check your MPEAS!");
        }
        user.setBindStatus(1);
        user.setBindTime(LocalDateTime.now());
        user.setEmail(StringUtils.replace(request.getEmail(), " ", ""));
        user.setAccountCvv(request.getAccountCvv().trim());
        user.setAccountExpireYear(request.getAccountExpireYear().trim());
//        user.setAccountExpireDay(request.getAccountExpireDay().trim());
        user.setAccountExpireMonth(request.getAccountExpireMonth().trim());
        user.setBankName(request.getBankName().trim());
        user.setBankCode(request.getBankCode().trim());

        int count = userService.updateByIdAndVersion(user);
        if (count == 0) {
            return BaseResponse.fail(TipConsts.CONFIRM_ERROR);
        }
        return BaseResponse.success(true);
    }

    @ApiOperation("提现")
    @PostMapping("/withdraw")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse withdraw(@RequestBody @Valid WithdrawRequest request) {
        //redis锁 单用户10秒才能请求一次
        Long userId = getUserId(request);
        if (!RedisUtils.setIfAbsent("payout" + userId, 10)) {
            return BaseResponse.fail("Operation too fast,Please try again!");
        }

        UserDO userDO = userService.get(userId);
        if (userDO == null || StringUtils.isBlank(userDO.getAccountPhone())) {
            return BaseResponse.fail("555", TipConsts.BANK_INFO_DEFECT);
        }

        //提现时间为工作日8~19点
//        String nineClock = RedisUtils.getString(DictConsts.WITHDRAW_START_TIME);
//        String eighteenClock = RedisUtils.getString(DictConsts.WITHDRAW_END_TIME);
//        String now = DateUtils.format(new Date(), DateUtils.SIMPLE_DATEFORMAT_HHMM);
//        if (DateUtils.checkWeekend() || now.compareTo(nineClock) < 0 || now.compareTo(eighteenClock) > 0) {
//            return BaseResponse.fail(RedisUtils.getString(DictConsts.WITHDRAW_TIME_TIP));
//        }

        //系统提现开关 0-关(不允许提现) 1-开(允许提现)
        Integer sysWithdrawalCheck = RedisUtils.getInteger(DictConsts.SYS_WITHDRAWAL_CHECK);
        if (SysWithdrawalCheckEnum.CLOSE.getCode().equals(sysWithdrawalCheck)) {
            String sysWithdrawalCheckTips = RedisUtils.getString(DictConsts.SYS_WITHDRAWAL_CHECK_TIPS);
            if (StringUtils.isBlank(sysWithdrawalCheckTips)) {
                sysWithdrawalCheckTips = "The payment channel is being upgraded, please wait for one hour.";
            }
            return BaseResponse.fail(sysWithdrawalCheckTips);
        }

        //个人提现开关 0-关 1-开
        if (WithdrawalCheckEnum.CLOSE.getCode().equals(userDO.getWithdrawCheck())) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.WITHDRAW_USER_CLOSE_TIP));
        }

        //支付渠道 1-跳转支付链接 2-三方rzp -1 -- 提现封盘
        String payChannel;

        //封盘开关 0-关 1-开
        Integer goPaySwitch = RedisUtils.getInteger(DictConsts.GO_PAY_SWITCH);
        if (goPaySwitch == null || goPaySwitch.equals(PayEnums.GoPaySwitchEnum.OPEN.getCode())) {
            payChannel = "-1";
        } else {
            payChannel = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL, String.class);
        }

        //客户提现金额
        BigDecimal withdrawAmount = request.getWithdrawAmount();
        //提现冻结金额key
        String withdrawalRedisKey = String.format(CacheConsts.ST_WITHDRAWAL, userId);
        if ("-1".equals(payChannel)) {
            BigDecimal stAmount = RedisUtils.getValue(withdrawalRedisKey, BigDecimal.class);
            if (stAmount == null) {
                stAmount = BigDecimal.ZERO;
            }
            if (stAmount.compareTo(BigDecimal.ZERO) > 0
                && userDO.getBalance().compareTo(stAmount.add(withdrawAmount)) < 0) {
                return BaseResponse.fail(
                    "The current account is not enough to withdraw cash, you have " + stAmount.toString()
                        + " rupees being paid");
            }
        }

        //当前余额 >= 提现金额
        if (userDO.getBalance().compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(TipConsts.CREDIT_RUNNING_LOW);
        }
        //未充值账户余额用户（仅购买vip也算未充值余额用户），永远不可提现。
        if (userDO.getFirstRecharge().equals(0)) {
            return BaseResponse.fail(TipConsts.NO_RECHARGE);
        }
        //获取用户充值成功金额
        PayVO payVO = payService.getCountByTypeStatus(Arrays.asList(1), 2, userId, null, null);
        if (userDO.getLxSwitch() == 0) {
            PayVO withdrawPayVO = payService.getCountByTypeStatus(Arrays.asList(99), 2, userId, null, null);
            if (payVO.getAmount().compareTo(withdrawPayVO.getAmount().add(withdrawAmount)) < 0) {
                return BaseResponse.fail(TipConsts.LX_SWITCH_CLOSE);
            }
        }
        //用户单笔提现额度大于充值额度2倍，同时提现金额大于5000，触发预警
        BigDecimal withdrawWarningAmount = RedisUtils.getValue(DictConsts.WITHDRAW_WARNING_AMOUNT, BigDecimal.class);
        if (withdrawAmount.compareTo(payVO.getAmount().multiply(new BigDecimal("2"))) >= 0
            && withdrawAmount.compareTo(withdrawWarningAmount) >= 0) {
            String content = StringCommonUtils
                .buildString("用户id--{},提现金额:{} ,远大于充值金额:{} ,请立刻查看", userId, withdrawAmount, payVO.getAmount());
        }

        /**
         * 初始化提现限制
         */
        //初始化提现手续费比例
        BigDecimal serviceRatio = new BigDecimal("0.18");
        if (StringUtils.isNotBlank(RedisUtils.getValue(DictConsts.SERVICE_RATIO, String.class))) {
            serviceRatio = RedisUtils.getValue(DictConsts.SERVICE_RATIO, BigDecimal.class);
        }
        //初始化VIP单笔限额
        BigDecimal vipSingleMaxQuota = BigDecimal.ZERO;
        //初始化VIP每日最大提现次数
        Integer vipSingleMaxCount = 1;

        if (userDO.getMemberLevel() != null) {
            if (StringUtils.isNotBlank(RedisUtils
                .getValue(String.format(DictConsts.VIP_SINGLE_MAX_WITHDRAW_QUOTA, userDO.getMemberLevel()),
                    String.class))) {
                vipSingleMaxQuota = RedisUtils
                    .getValue(String.format(DictConsts.VIP_SINGLE_MAX_WITHDRAW_QUOTA, userDO.getMemberLevel()),
                        BigDecimal.class);
            }
            if (StringUtils.isNotBlank(RedisUtils
                .getValue(String.format(DictConsts.VIP_TODAY_MAX_WITHDRAW_COUNT, userDO.getMemberLevel()),
                    String.class))) {
                vipSingleMaxCount = RedisUtils
                    .getValue(String.format(DictConsts.VIP_TODAY_MAX_WITHDRAW_COUNT, userDO.getMemberLevel()),
                        Integer.class);
            }
        } else {
            return BaseResponse.fail(TipConsts.WITHDRAWAL_COUNT_LOW);
        }

        //初始化系统每日最大提现限额
        BigDecimal sysTodayMaxWithdrawQuota = BigDecimal.ZERO;
        //获取系统每日最大提现限额
        if (StringUtils.isNotBlank(RedisUtils.getValue(DictConsts.SYS_TODAY_MAX_WITHDRAW_QUOTA, String.class))) {
            sysTodayMaxWithdrawQuota = new BigDecimal(
                RedisUtils.getValue(DictConsts.SYS_TODAY_MAX_WITHDRAW_QUOTA, String.class));
        }

        //初始化提现最低限额
        BigDecimal singeLowWithdrawQuota = new BigDecimal("100");
        //获取提现最低限额
        if (StringUtils.isNotBlank(RedisUtils.getValue(DictConsts.SINGE_LOW_WITHDRAW_QUOTA, String.class))) {
            singeLowWithdrawQuota = new BigDecimal(
                RedisUtils.getValue(DictConsts.SINGE_LOW_WITHDRAW_QUOTA, String.class));
        }

        //获取当前用户今日历史提现信息
        WithdrawalRecordDO localDateUserInfo = withdrawalRecordService.getLocalDateInfoByUserId(userId);

        //获取当前系统资金池剩余提现余额
        WithdrawalRecordDO localDateSysInfo = withdrawalRecordService.getLocalDateSysInfo();

        /**
         * 规则校验
         */
        //提现金额 >= 提现最低限额
        if (singeLowWithdrawQuota.compareTo(withdrawAmount) > 0) {
            return BaseResponse.fail(TipConsts.SINGLE_WITHDRAW_LOW_OF_AMOUNT + singeLowWithdrawQuota);
        }

        //VIP 单笔最大限额 >= 提现金额
        if (vipSingleMaxQuota.compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(TipConsts.SINGLE_BALANCE_OUT_OF_LIMIT);
        }

        //VIP每日最大提现次数 > 当日提现次数
        if (localDateUserInfo != null && vipSingleMaxCount.compareTo(localDateUserInfo.getCount()) == 0) {
            return BaseResponse.fail(TipConsts.WITHDRAWAL_COUNT_LOW);
        }

        //系统每日最大提现限额 >= 当前提现金额
        if (sysTodayMaxWithdrawQuota.compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(TipConsts.SINGLE_BALANCE_OUT_OF_LIMIT);
        }

        //当日系统提现资金池余额 >= 提现金额
        if (localDateSysInfo != null
            && localDateSysInfo.getTotalAmount().subtract(localDateSysInfo.getChangeAmount()).compareTo(withdrawAmount)
            < 0) {
            return BaseResponse.fail(TipConsts.SYS_CREDIT_RUNNING_LOW);
        }

        /**
         * 创建提现单
         */
        //提现手续费
        BigDecimal serviceCharge = withdrawAmount.multiply(serviceRatio);
        //用户实际到账金额
        BigDecimal relAccount = withdrawAmount.subtract(serviceCharge).setScale(2, RoundingMode.DOWN);

        //保存系统提现信息
        if (localDateSysInfo == null) {
            localDateSysInfo = new WithdrawalRecordDO();
            localDateSysInfo.setTotalAmount(sysTodayMaxWithdrawQuota);
            localDateSysInfo.setChangeAmount(withdrawAmount);
            localDateSysInfo.setChangeTax(serviceCharge);
            localDateSysInfo.setUserId(-1L);
            withdrawalRecordService.initSysData(localDateSysInfo);
        } else {
            localDateSysInfo.setChangeAmount(withdrawAmount);
            localDateSysInfo.setChangeTax(serviceCharge);
            withdrawalRecordService.changeAmount(localDateSysInfo);
        }

        //保存用户提现信息
        if (localDateUserInfo == null) {
            localDateUserInfo = new WithdrawalRecordDO();
            localDateUserInfo.setTotalAmount(sysTodayMaxWithdrawQuota);
            localDateUserInfo.setChangeAmount(withdrawAmount);
            localDateUserInfo.setChangeTax(serviceCharge);
            localDateUserInfo.setUserId(userId);
            withdrawalRecordService.initUserData(localDateUserInfo);
        } else {
            localDateUserInfo.setChangeAmount(withdrawAmount);
            localDateUserInfo.setChangeTax(serviceCharge);
            withdrawalRecordService.changeAmount(localDateUserInfo);
        }

        //支付渠道分支 1-wegame 2-paystax
        String payChannelBranch = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL_BRANCH, String.class);
        String payoutId = "";
        //生成随机订单号
        String orderFirstNo = userId + "o";
        String orderNo = orderFirstNo + StringRandom.getNumberAndLetterRandom(12 - orderFirstNo.length());
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payChannelBranch);
        //找到支付实现
        PaymentService paymentService = SpringContextUtils.getBean(payBeanName);
//        CreatePayOutOrder createPayOutOrder = new CreatePayOutOrder();
//        createPayOutOrder.setUserDO(userDO);
//        createPayOutOrder.setAmount(relAccount);
//        createPayOutOrder.setOrderNo(orderNo);
        /**
         * 扣减余额 封盘不扣减
         */
        if (!"-1".equals(payChannel)) {
            BalanceDO balanceDO = new BalanceDO();
            balanceDO.setType(BalanceTypeEnum.G.getCode());
            balanceDO.setUserId(userId);
            balanceDO.setAmount(BigDecimal.ZERO.subtract(withdrawAmount));
            balanceDO.setWithdrawAmount(BigDecimal.ZERO.subtract(relAccount));
            balanceDO.setServiceFee(BigDecimal.ZERO.subtract(serviceCharge));
            balanceDO.setStatus(StatusTypeEnum.WAIT.getCode());
            balanceDO.setCreateTime(new Date());
            balanceDO.setUpdateTime(new Date());
            balanceDO.setOrderNo(orderNo);
            int updateBalanceCount = balanceService.addBalanceDetail(balanceDO);
            if (updateBalanceCount <= 0) {
                throw new BaseException("submit withdraw error,please try again!");
            }
        }
        String applySwitch = RedisUtils.getString(DictConsts.APPLY_SWITCH);
        if ("1".equals(applySwitch)) {
            logger.info("{}开始提现审批,提现金额:{},本地订单号:{}", userId, relAccount, orderNo);
            //审批开关开启走审批流程
            return applyWithdraw(orderNo, userId, relAccount, userDO.getSaleId());
        }

        logger.info("{}开始提现,提现金额:{},本地订单号:{}", userId, relAccount, orderNo);
        /**
         * 调用提现接口
         */
        if ("-1".equals(payChannel)) {
            payoutId = "order_stw_" + userId + "_" + StringRandom.getNumberAndLetterRandom(10);
            BigDecimal stAmount = RedisUtils.getValue(withdrawalRedisKey, BigDecimal.class);
            if (stAmount == null) {
                RedisUtils.setValue(withdrawalRedisKey, withdrawAmount.toString());
            } else {
                RedisUtils.setValue(withdrawalRedisKey, stAmount.add(withdrawAmount).toString());
            }
        } else if ("1".equals(payChannel)) {
            CreatePayOutOrder createPayOutOrder = new CreatePayOutOrder();
            createPayOutOrder.setUserDO(userDO);
            createPayOutOrder.setOrderNo(orderNo);
            createPayOutOrder.setAmount(relAccount);
            BaseResponse<PaymentResult> payout = paymentService.payout(createPayOutOrder);
            if (payout == null) {
                throw new BaseException("submit withdraw error,please try again!");
            }
            payoutId = payout.getResultData().getThirdOrderNo();
        }

        logger.info("用户id:{},提现金额:{},订单号:{},对方订单号:{}", userId, relAccount, orderNo, payoutId);

        /**
         * 添加pay信息
         */
        PayDO payDO = new PayDO();
        payDO.setOrderNo(orderNo);
        payDO.setType(PayEnums.PayTypeEnum.WITHDRAW.getCode());
        payDO.setThirdNo(payoutId);
        //提现会话id
        payDO.setThirdResponse(payoutId);
        payDO.setUserId(userId);
        payDO.setAmount(relAccount);
        payDO.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        payDO.setPayChannel(payChannel);
        payDO.setPayChannelBranch(payChannelBranch);
        payDO.setRefNo(orderNo);
        payDO.setSaleId(userDO.getSaleId());
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
        int saveSuccess = payService.save(payDO);
        if (saveSuccess <= 0) {
            throw new BaseException("submit withdraw error,please try again!");
        }
        logger.info("{}提现入库,提现金额:{},订单号:{},对方订单号:{}", userId, relAccount, orderNo, payoutId);
        return BaseResponse.success();
    }

    @ApiOperation("成就")
    @PostMapping("/mission")
    @Transactional
    public BaseResponse<MissionResponse> mission(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        MissionResponse response = new MissionResponse();
        response.setTopTips(RedisUtils.getString(DictConsts.MISSION_TOP_TIPS));
        response.setTaskAmount(new BigDecimal(RedisUtils.getString(DictConsts.MISSION_TASK_AMOUNT)));
        List<UserDO> totalPeople = userService.getTotalPeople(userId);
        if (CollectionUtils.isNotEmpty(totalPeople)) {
            response.setTotalPeople(totalPeople.size());
            response.setTotalComplete(totalPeople.stream().filter(o -> o.getFirstRecharge() == 1).count());
        } else {
            response.setTotalPeople(0);
            response.setTotalComplete(0L);
        }
        return BaseResponse.success(response);
    }

    @ApiOperation("新成就")
    @PostMapping("/newMission")
    @Transactional
    public BaseResponse<NewMissionResponse> newMission(@RequestBody @Valid BaseRequest request) {
        Long userId = getUserId(request);
        NewMissionResponse response = new NewMissionResponse();
        List<UserDO> totalPeople = userService.getTotalPeople(userId);
        if (CollectionUtils.isNotEmpty(totalPeople)) {
            response.setTotalPeople(totalPeople.size());
            response.setTotalComplete(totalPeople.stream().filter(o -> o.getFirstRecharge() == 1).count());
        } else {
            response.setTotalPeople(0);
            response.setTotalComplete(0L);
        }
        if (response.getTotalPeople() > 0) {

        }
        return BaseResponse.success(response);
    }

    /**
     * 创建提现审批
     */
    private BaseResponse applyWithdraw(String orderNo, Long userId, BigDecimal relAccount, Long saleId) {
        /**
         * 添加pay信息
         */
        PayDO payDO = new PayDO();
        payDO.setOrderNo(orderNo);
        payDO.setType(PayEnums.PayTypeEnum.WITHDRAW.getCode());
        payDO.setUserId(userId);
        payDO.setAmount(relAccount);
        payDO.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        payDO.setRefNo(orderNo);
        payDO.setSaleId(saleId);
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.APPLY.getCode());
        int saveSuccess = payService.save(payDO);
        if (saveSuccess <= 0) {
            throw new BaseException("submit withdraw error,please try again!");
        }
        return BaseResponse.success();
    }

    @Transactional
    @PostMapping("/pass")
    public BaseResponse applyPass(@RequestBody ApplyRequest applyRequest) {
        //redis锁,单笔订单每5秒可处理一次
        if (!RedisUtils.setIfAbsent(applyRequest.getOrderNo(), 5)) {
            return BaseResponse.fail("Operation too fast,Please try again!");
        }

        PayDO payDO = payService.getOrderNo(applyRequest.getOrderNo());
        if (!PayEnums.PayApplyStatusEnum.APPLY.getCode().equals(payDO.getApplyStatus())) {
            return BaseResponse.success();
        }
        UserDO userDO = userService.get(payDO.getUserId());

        CreatePayOutOrder createPayOutOrder = new CreatePayOutOrder();
        createPayOutOrder.setUserDO(userDO);
        createPayOutOrder.setOrderNo(applyRequest.getOrderNo());
        createPayOutOrder.setAmount(payDO.getAmount());
        createPayOutOrder.setUserDO(userDO);

        String payChannel = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL, String.class);
        String payChannelBranch = "10";
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payChannelBranch);
        //        String payBeanName = PaymentServiceEnum.getBeanNameByCode("3");
        //找到支付实现
        PaymentService paymentService = (PaymentService) SpringContextUtils.getBean(payBeanName);

        BaseResponse<PaymentResult> payout = paymentService.payout(createPayOutOrder);
        if (payout == null) {
            throw new BaseException("submit withdraw error,please try again!");
        }
        String payoutId = payout.getResultData().getThirdOrderNo();
        payDO.setThirdNo(payoutId);
        //提现会话id
        payDO.setThirdResponse(payoutId);
        payDO.setPayChannel(payChannel);
        payDO.setPayChannelBranch(payChannelBranch);
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
        payService.updatePay(payDO);
        return BaseResponse.success();
    }

    @Transactional
    @PostMapping("/reject")
    public BaseResponse applyReject(@RequestBody ApplyRequest applyRequest) {
        PayDO payDO = payService.getOrderNo(applyRequest.getOrderNo());
        if (!PayEnums.PayApplyStatusEnum.APPLY.getCode().equals(payDO.getApplyStatus())) {
            return BaseResponse.success();
        }
        payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.REJECT.getCode());
        payDO.setRemark(applyRequest.getRemark());
        payService.applyReject(payDO);
        return BaseResponse.success();
    }

    @ApiOperation("提现文案")
    @GetMapping("/withdrawAmountDoc")
    public BaseResponse<WithdrawAmountDoc> getWithdrawAmountDoc() {
        WithdrawAmountDoc withdrawAmountDoc = new WithdrawAmountDoc();
        String minAmount = RedisUtils.getString(DictConsts.WITHDRAW_AMOUNT_MIN);
        withdrawAmountDoc.setMinimumWithdrawAmount(new BigDecimal(minAmount));
        withdrawAmountDoc.setWithdrawInputText("Enter " + minAmount + " and above");
        withdrawAmountDoc.setWithdrawInputTextColor(RedisUtils.getString(DictConsts.WITHDRAW_AMOUNT_TEXT_COLOR));
        withdrawAmountDoc.setTips(RedisUtils.getString(DictConsts.WITHDRAW_AMOUNT_TIPS));
        return BaseResponse.success(withdrawAmountDoc);
    }

}
