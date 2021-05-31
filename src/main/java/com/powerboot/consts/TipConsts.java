package com.powerboot.consts;

import java.math.BigDecimal;

/**
 * @description : 提示语
 * @date : Created in 2020/7/16 15:48
 **/
public class TipConsts {

    //出错提示重试
    public static final String TRY_AGAIN = "this fails, Please try again!";

    //手机号非空
    public static final String MOBILE_NOT_EMPTY = "Mobile number can't be empty!";

    public static final String OTP_NOT_EMPTY = "OTP can't be empty!";
    //验证码非空

    //验证码过期
    public static final String OTP_EXPIRED = "OTP expired, Please resend OTP!";

    //验证码不匹配
    public static final String OTP_MISMATCH = "Invalid verification code,please try again!";

    //未登录
    public static final String NO_LOGIN = "Please log in first!";

    //提交错误
    public static final String CONFIRM_ERROR = "Confirm error, Please try again!";

    //字符太长
    public static final String CHARACTER_TOO_LONG = "Input too long!";

    //金额为空
    public static final String SELECT_AMOUNT = "Please select amount!";

    //期限为空
    public static final String SELECT_TERM = "Please select term!";

    //ip黑名单提示
    public static final String IP_BLACK_LIST_TIP = "System Error!";

    //未注册
    public static final String NO_REGISTER = "Account not registered Please register!";

    //密码错误
    public static final String PASSWORD_ERROR = "Wrong phone number or password，please re-enter!";

    //错误手机号
    public static final String MOBILE_ERROR = "Unknown Mobile!";

    //手机已注册
    public static final String MOBILE_REGISTERED = "This phone number has been registered,please try again!";

    //银行信息不完善
    public static final String BANK_INFO_DEFECT = "Please complete the binding your information.";

    //余额不足
    public static final String CREDIT_RUNNING_LOW = "Sorry, your credit is running low!";

    //系统余额不足
    public static final String SYS_CREDIT_RUNNING_LOW = "Sorry, system credit is running low!";
    //提现次数不足
    public static final String WITHDRAWAL_COUNT_LOW = "The number of withdrawals on the day has been used up, please upgrade your VIP level!";

    //超出单笔最大限制
    public static final String SINGLE_BALANCE_OUT_OF_LIMIT = "Maximum limit of single transaction exceeded";

    //单笔提现最低
    public static final String SINGLE_WITHDRAW_LOW_OF_AMOUNT = "Single withdraw can not low of";

    //操作太快请测试
    public static final String OPERATION_FAST = "Operation is too fast, please try again";

    //相同ip注册过多
    public static final String IP_REGISTER_COUNT = "Please follow the rules of the platform and do not register maliciously";
    //相同银行卡多次绑定
    public static final String BANK_CARD_REPEAT = "Duplicate bank card, each user can only bind one bank card";
    //没有充值不让提现
    public static final String NO_RECHARGE = "Your account is abnormal, please contact customer service";

    //用户利息开关关闭提现警告
    public static final String LX_SWITCH_CLOSE = "The account has been restricted, you can only withdraw the principal,please contact Customer Service.";

    //余额不足
    public static final String CREATE_ORDER_BALANCE_FAIL = "Your account balance is insufficient, please recharge it ";

    /**
     * 修改密码关闭提示
     */
    public static final String MODIFY_PASSWORD_NOT_SUPPORT = "Before the platform upgrade, password modification is not supported. Please contact customer service staff to reset your password.";

    public static final String VIP_UPDATE_COUNT_FAIL = "Due to insufficient invitation: the valid user task you invited has not been completed and cannot be upgraded!";

    public static final String BLACK_LOGIN_FAIL = "Due to your repeated registration, this account is blocked by us !";

    /**
     * 重复设备登陆错误
     */
    public static final String REPETITION_DEVICE_LOGIN_FAIL = "Please comply with the rules of the platform, do not repeat registration!";

    /**
     * 支付系统异常
     */
    public static final String PAYMENT_SYSTEM_FAIL = "The payment system is being upgraded, please wait for 1 hour";

    /**
     * 登录超时异常
     */
    public static final String LOGIN_TIMEOUT_FAIL = "login timeout, please login first";

    /**
     * 异常支付
     */
    public static final String UNKNOWN_PAY_FAIL = "Unknown Pay!!!";

    /**
     * 购买理财产品异常
     */
    public static final String CREATE_FINANCIAL_PRODUCT_FAIL = "The purchase failed, please try again ";

    /**
     * 订单不存在异常
     */
    public static final String ORDER_NOT_FOUND_FAIL = "This order not found";

    /**
     * 参数错误
     */
    public static final String PARAMS_FAIL = "error params";

    /**
     * 支付异常
     */
    public static final String PAYMENT_FAIL = "Unknown error : Your payment was fail,please try again later!";

    /**
     * 请求次数限制异常
     */
    public static final String REQUEST_LIMIT_FAIL = "Request over limits!";

    /**
     * 发送消息异常
     */
    public static final String SEND_MESSAGE_FAIL = "Send message error";

    /**
     * 手机号错误异常
     */
    public static final String MOBILE_NUMBER_FAIL = "Wrong mobile number.";

    /**
     * 注册异常
     */
    public static final String REGISTER_FAIL = "register fall,please try again!";

    /**
     * 修改密码异常
     */
    public static final String MODIFY_PASSWORD_FAIL = "modify password fall,please try again!";

    /**
     * 系统异常提醒重启
     */
    public static final String SYSTEM_FAIL_CLOSE_RETRY = "Your system error, Please close app , Try open again";

    /**
     * 起购金额提示
     */
    public static final String FINANCIAL_MORE_TIP = "A single purchase required more than ";

    /**
     * 单笔上限提示
     */
    public static final String FINANCIAL_LESS_TIP = "A single purchase required less than ";

    /**
     * 余额不足
     */
    public static final String BALANCE_INSUFFICIENT_FAIL = "Your balance is insufficient, Please recharge first";

    /**
     * 产品VIP等级不足错误
     */
    public static final String PRODUCT_VIP_MEMBER_LOWER_FAIL = "The product need Member VIP %s and higher, Please purchase Member VIP first";

    /**
     * 产品最大限购额错误
     */
    public static final String PRODUCT_TOTAL_PURCHASE_FAIL = " total purchase limit is %s, You have purchased %s, Please modify your input amount";

    /**
     * 账户被限额错误
     */
    public static final String ACCOUNT_RESTRICTED_FAIL = "The account has been restricted,please contact Customer Service";

    /**
     * 订单数量限制错误
     */
    public static final String ORDER_UPPER_LIMIT_FAIL = "The order quantity has reached the upper limit.";

    /**
     * 回到首页并刷新提示
     */
    public static final String GO_HOME_REFRESH = "Please go back to the home page and refresh again.";

    /**
     * 错误金额
     */
    public static final String AMOUNT_FAIL = "error amount";

    /**
     * 错误利率
     */
    public static final String RATE_FAIL = "error rate";

    /**
     * 充值最低金额提示
     */
    public static final String RECHARGE_LOW_FAIL = "Recharge can not low of 300!";

    /**
     * 当天充值限制
     */
    public static final String RECHARGE_LIMIT_DAY = "You can only recharge %s a day";

    /**
     * 系统异常
     */
    public static final String SYSTEM_FAIL = "System error";

    /**
     * 任务完成提示
     */
    public static final String ALL_TASK_COMPLETE = "All tasks have been completed today";

    /**
     * 购买会员卡标题
     */
    public static final String MEMBER_INFO_TITLE = "Players will be LV1 members by default after registering an account";

    /**
     * 修改失败
     */
    public static final String MODIFY_FAIL = "modify fall";

    /**
     * 提现金额不足
     */
    public static final String WITHDRAW_NOT_ENOUGH_FAIL = "The current account is not enough to withdraw cash, you have %s rupees being paid";

    /**
     * 提现提交失败
     */
    public static final String SUBMIT_WITHDRAW_FAIL = "submit withdraw error,please try again!";
}
