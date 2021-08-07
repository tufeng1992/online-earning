package com.powerboot.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.mapper.Condition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.StringCommonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.consts.*;
import com.powerboot.dao.MemberInfoDao;
import com.powerboot.domain.*;
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
import com.powerboot.service.*;
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
import java.util.*;
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
    @Autowired
    private BlackUserLogService blackUserLogService;

    @Autowired
    private MemberInfoDao memberInfoDao;

    @Autowired
    private LoginLogService loginLogService;

    @PostMapping("/memberInfoNew")
    @ApiOperation(value = "购买会员卡页面-新")
    public BuyMemberInfoResponse<List<MemberInfoDescDto>> memberInfoNew(@RequestBody @Valid BaseRequest request) {
        BuyMemberInfoResponse<List<MemberInfoDescDto>> response = new BuyMemberInfoResponse();
        Long userId = getUserId(request);
        List<UserDO> userDOList = userService.getUserByParentId(userId);
        //完成登录的子集用户数量
        int childFirstRechargedCount = 0;
        if (CollectionUtils.isNotEmpty(userDOList)){
            for (UserDO aDo : userDOList) {
                Map<String, Object> loginLogParams = Maps.newHashMap();
                loginLogParams.put("userId", aDo.getId());
                int loginCount = loginLogService.count(loginLogParams);
                if (loginCount > 0) {
                    childFirstRechargedCount++;
                }
            }
        }
        List<MemberInfoDescDto> list = new ArrayList<>();
        response.setTitle(I18nEnum.MEMBER_INFO_TITLE.getMsg());

        List<MemberInfoDO> memberInfoList = memberInfoDao.selectList(new Condition());
        int finalChildFirstRechargedCount = childFirstRechargedCount;
        for (MemberInfoDO memberInfoDO : memberInfoList) {
            MemberInfoDescDto vip = new MemberInfoDescDto();
            BeanUtil.copyProperties(memberInfoDO, vip);
            vip.setAmount(memberInfoDO.getAmount());
            vip.setOrderTimes(memberInfoDO.getOrderTimes());
            vip.setBuyVipCondition(String.format(vip.getBuyVipCondition(), finalChildFirstRechargedCount));
            vip.setClickButton(memberInfoDO.getUpLimit() <= childFirstRechargedCount);
            list.add(vip);
        }
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
        return userService.updateByIdAndVersion(userDO) > 0 ? BaseResponse.success() : BaseResponse.fail(I18nEnum.MODIFY_FAIL.getMsg());
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
            return BaseResponse.fail(I18nEnum.NO_LOGIN.getMsg());
        }
        //风控校验
        blackUserService.blackCheck(request.getMobile(), request.getName(), request.getEmail(), userId);
        UserDO user = userService.get(userId);
        if (user == null) {
            return BaseResponse.fail(I18nEnum.NO_LOGIN.getMsg());
        }
        user.setFirstName(StringUtils.trim(request.getFirstName()));
        user.setName(StringUtils.trim(request.getName()));
        user.setLastName(StringUtils.trim(request.getLastName()));
        String phone = StringUtils.replace(request.getMobile(), " ", "");
        //补全手机号
        phone = MobileUtil.replaceValidMobile(phone);

        if (MobileUtil.isValidMobile(phone)) {
            user.setAccountPhone(phone);
        } else {
            return BaseResponse.fail(I18nEnum.MOBILE_NUMBER_FAIL.getMsg());
        }
        user.setBindStatus(1);
        user.setBindTime(LocalDateTime.now());
        user.setEmail(StringUtils.replace(request.getEmail(), " ", ""));
        if (StringUtils.isNotBlank(request.getAccountCvv())) {
            user.setAccountCvv(request.getAccountCvv().trim());
        }
        if (StringUtils.isNotBlank(request.getAccountExpireYear())) {
            user.setAccountExpireYear(request.getAccountExpireYear().trim());
        }
//        user.setAccountExpireDay(request.getAccountExpireDay().trim());
        if (StringUtils.isNotBlank(request.getAccountExpireMonth())) {
            user.setAccountExpireMonth(request.getAccountExpireMonth().trim());
        }
        if (StringUtils.isNotBlank(request.getBankName())) {
            user.setBankName(request.getBankName().trim());
        }
        user.setBankCode(request.getBankCode().trim());
        user.setAccountNumber(request.getAccountNumber().trim());

        boolean checkBlack = false;
        List<UserDO> userDOList = userService.selectByAccountNumber(user.getAccountNumber());
        if (CollectionUtils.isNotEmpty(userDOList)) {
            for (UserDO userDO : userDOList) {
                if (!userDO.getId().equals(user.getId())) {
                    checkBlack = true;
                    user.setLoginFlag(0);
                    user.setBlackFlag(1);
                    break;
                }
            }
        }
        int count = userService.updateByIdAndVersion(user);
        if (count == 0) {
            return BaseResponse.fail(I18nEnum.CONFIRM_ERROR.getMsg());
        }
        if (checkBlack) {
            blackUserLogService.addBlackUserLog(user.getId(), "银行绑卡重复：" + user.getAccountNumber(), user.getSaleId());
            return BaseResponse.fail(I18nEnum.BLACK_LOGIN_FAIL.getMsg());
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
            return BaseResponse.fail(I18nEnum.OPERATION_FAST.getMsg());
        }

        UserDO userDO = userService.get(userId);
        if (userDO == null || StringUtils.isBlank(userDO.getAccountPhone())) {
            return BaseResponse.fail("555", I18nEnum.BANK_INFO_DEFECT.getMsg());
        }
        if (StringUtils.isEmpty(userDO.getAccountNumber())) {
            return BaseResponse.fail(I18nEnum.PAY_BIND_CARD_FAIL.getCode(), I18nEnum.PAY_BIND_CARD_FAIL.getMsg());
        }
        if (StringUtils.isEmpty(userDO.getName())) {
            return BaseResponse.fail(I18nEnum.PAY_NAME_FAIL.getCode(), I18nEnum.PAY_NAME_FAIL.getMsg());
        }

        //提现时间为工作日8~19点
        String nineClock = RedisUtils.getString(DictConsts.WITHDRAW_START_TIME);
        String eighteenClock = RedisUtils.getString(DictConsts.WITHDRAW_END_TIME);
        String now = DateUtils.format(new Date(), DateUtils.SIMPLE_DATEFORMAT_HHMM);
        if (DateUtils.checkWeekend() || now.compareTo(nineClock) < 0 || now.compareTo(eighteenClock) > 0) {
            return BaseResponse.fail(RedisUtils.getString(DictConsts.WITHDRAW_TIME_TIP));
        }

        //系统提现开关 0-关(不允许提现) 1-开(允许提现)
        Integer sysWithdrawalCheck = RedisUtils.getInteger(DictConsts.SYS_WITHDRAWAL_CHECK);
        if (SysWithdrawalCheckEnum.CLOSE.getCode().equals(sysWithdrawalCheck)) {
            String sysWithdrawalCheckTips = RedisUtils.getString(DictConsts.SYS_WITHDRAWAL_CHECK_TIPS);
            if (StringUtils.isBlank(sysWithdrawalCheckTips)) {
                sysWithdrawalCheckTips = I18nEnum.PAYMENT_SYSTEM_FAIL.getMsg();
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
                return BaseResponse.fail(String.format(I18nEnum.WITHDRAW_NOT_ENOUGH_FAIL.getMsg(), stAmount.toString()));
            }
        }

        //当前余额 >= 提现金额
        if (userDO.getBalance().compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(I18nEnum.CREDIT_RUNNING_LOW.getMsg());
        }
        //未充值账户余额用户（仅购买vip也算未充值余额用户），永远不可提现。
        if (userDO.getFirstRecharge().equals(0)) {
            return BaseResponse.fail(I18nEnum.NO_RECHARGE.getMsg());
        }
        //获取用户充值成功金额
        PayVO payVO = payService.getCountByTypeStatus(Arrays.asList(1), 2, userId, null, null);
        if (userDO.getLxSwitch() == 0) {
            PayVO withdrawPayVO = payService.getCountByTypeStatus(Arrays.asList(99), 2, userId, null, null);
            if (payVO.getAmount().compareTo(withdrawPayVO.getAmount().add(withdrawAmount)) < 0) {
                return BaseResponse.fail(I18nEnum.LX_SWITCH_CLOSE.getMsg());
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
            return BaseResponse.fail(I18nEnum.WITHDRAWAL_COUNT_LOW.getMsg());
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
            return BaseResponse.fail(I18nEnum.SINGLE_WITHDRAW_LOW_OF_AMOUNT.getMsg() + singeLowWithdrawQuota);
        }

        //VIP 单笔最大限额 >= 提现金额
        if (vipSingleMaxQuota.compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(I18nEnum.SINGLE_BALANCE_OUT_OF_LIMIT.getMsg());
        }

        //VIP每日最大提现次数 > 当日提现次数
        if (localDateUserInfo != null && vipSingleMaxCount.compareTo(localDateUserInfo.getCount()) == 0) {
            return BaseResponse.fail(I18nEnum.WITHDRAWAL_COUNT_LOW.getMsg());
        }

        //系统每日最大提现限额 >= 当前提现金额
        if (sysTodayMaxWithdrawQuota.compareTo(withdrawAmount) < 0) {
            return BaseResponse.fail(I18nEnum.SINGLE_BALANCE_OUT_OF_LIMIT.getMsg());
        }

        //当日系统提现资金池余额 >= 提现金额
        if (localDateSysInfo != null
            && localDateSysInfo.getTotalAmount().subtract(localDateSysInfo.getChangeAmount()).compareTo(withdrawAmount)
            < 0) {
            return BaseResponse.fail(I18nEnum.SYS_CREDIT_RUNNING_LOW.getMsg());
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
        Integer payoutStatus = null;
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
                throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
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
                throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
            }
            if (!payout.isSuccess()) {
                throw new BaseException(payout.getMsg());
            }
            payoutStatus = payout.getResultData().getStatus();
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
        if (null != payoutStatus) {
            payDO.setStatus(payoutStatus);
        } else {
            payDO.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
        }
        payDO.setPayChannel(payChannel);
        payDO.setPayChannelBranch(payChannelBranch);
        payDO.setRefNo(orderNo);
        payDO.setSaleId(userDO.getSaleId());
        payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
        int saveSuccess = payService.save(payDO);
        if (saveSuccess <= 0) {
            throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
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
            throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
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
        if (!PayEnums.PayApplyStatusEnum.APPLY.getCode().equals(payDO.getApplyStatus())
                && !PayEnums.PayApplyStatusEnum.PASS_WAIT.getCode().equals(payDO.getApplyStatus())) {
            return BaseResponse.success();
        }
        UserDO userDO = userService.get(payDO.getUserId());

        CreatePayOutOrder createPayOutOrder = new CreatePayOutOrder();
        createPayOutOrder.setUserDO(userDO);
        createPayOutOrder.setOrderNo(applyRequest.getOrderNo());
        createPayOutOrder.setAmount(payDO.getAmount());
        createPayOutOrder.setUserDO(userDO);

        String payChannel = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL, String.class);
        String payChannelBranch = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL_BRANCH, String.class);
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payChannelBranch);
        //找到支付实现
        PaymentService paymentService = SpringContextUtils.getBean(payBeanName);

        BaseResponse<PaymentResult> payout = paymentService.payout(createPayOutOrder);
        if (payout == null) {
            throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
        }
        payDO.setPayChannel(payChannel);
        payDO.setPayChannelBranch(payChannelBranch);
        if (payout.isSuccess()) {
            payDO.setThirdNo(payout.getResultData().getThirdOrderNo());
            //提现会话id
            payDO.setThirdResponse(payout.getResultData().getDescription());
            payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
            if (null != payout.getResultData().getStatus()) {
                payDO.setStatus(payout.getResultData().getStatus());
            }
            payService.updatePay(payDO);
        } else {
            payDO.setThirdResponse(payout.getMsg());
            if (StringUtils.isNotBlank(payDO.getThirdResponse())) {
                payDO.setRemark(payout.getMsg());
            }
            payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.REJECT.getCode());
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, payBeanName);
        }
        return BaseResponse.success();
    }

    @Transactional
    @PostMapping("/passBatch")
    public BaseResponse applyPassBatch(@RequestBody List<ApplyRequest> applyRequestList) {
        List<CreatePayOutOrder> createPayOutOrderList = Lists.newArrayList();
        List<PayDO> payDOList = Lists.newArrayList();
        for (ApplyRequest applyRequest : applyRequestList) {
            PayDO payDO = payService.getOrderNo(applyRequest.getOrderNo());
            if (!PayEnums.PayApplyStatusEnum.APPLY.getCode().equals(payDO.getApplyStatus())
                    && !PayEnums.PayApplyStatusEnum.PASS_WAIT.getCode().equals(payDO.getApplyStatus())) {
                return BaseResponse.success();
            }
            UserDO userDO = userService.get(payDO.getUserId());
            CreatePayOutOrder createPayOutOrder = new CreatePayOutOrder();
            createPayOutOrder.setUserDO(userDO);
            createPayOutOrder.setOrderNo(applyRequest.getOrderNo());
            createPayOutOrder.setAmount(payDO.getAmount());
            createPayOutOrder.setUserDO(userDO);
            createPayOutOrderList.add(createPayOutOrder);
            payDOList.add(payDO);
        }
        String payChannel = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL, String.class);
        String payChannelBranch = RedisUtils.getValue(DictConsts.PAYOUT_CHANNEL_BRANCH, String.class);
        //获取支付bean
        String payBeanName = PaymentServiceEnum.getBeanNameByCode(payChannelBranch);
        //找到支付实现
        PaymentService paymentService = SpringContextUtils.getBean(payBeanName);
        BaseResponse<PaymentResult> payout = paymentService.payoutBatch(createPayOutOrderList);
        if (payout == null) {
            throw new BaseException(I18nEnum.SUBMIT_WITHDRAW_FAIL.getMsg());
        }
        for (PayDO payDO : payDOList) {
            payDO.setPayChannel(payChannel);
            payDO.setPayChannelBranch(payChannelBranch);
            if (payout.isSuccess()) {
                payDO.setThirdNo(payout.getResultData().getThirdOrderNo());
                //提现会话id
                payDO.setThirdResponse(payout.getResultData().getDescription());
                payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.PASS.getCode());
                if (null != payout.getResultData().getStatus()) {
                    payDO.setStatus(payout.getResultData().getStatus());
                }
                payService.updatePay(payDO);
            } else {
                payDO.setThirdResponse(payout.getMsg());
                if (StringUtils.isNotBlank(payDO.getThirdResponse())) {
                    payDO.setRemark(payout.getMsg());
                }
                payDO.setApplyStatus(PayEnums.PayApplyStatusEnum.REJECT.getCode());
                payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
                payService.payoutFail(payDO, payBeanName);
            }
        }
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

        withdrawAmountDoc.setWithdrawInputText(String.format(I18nEnum.WITHDRAW_INPUT_TEXT.getMsg(), minAmount));
        withdrawAmountDoc.setWithdrawInputTextColor(RedisUtils.getString(DictConsts.WITHDRAW_AMOUNT_TEXT_COLOR));
        withdrawAmountDoc.setTips(RedisUtils.getString(DictConsts.WITHDRAW_AMOUNT_TIPS));
        return BaseResponse.success(withdrawAmountDoc);
    }


    public static void main(String[] args) {
        String freeStr = "ส่วนลดจากการส่งเสริมการขายคือ %s%%/ %s%%/ %s%%";
        System.out.println(String.format(freeStr, "t1", "t2", "t3"));
    }
}
