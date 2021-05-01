package com.powerboot.consts;

/**
 * @description : 提示语
 * @date : Created in 2020/7/16 15:48
 **/
public class TipConsts {

    //出错提示重试
    public static final String TRY_AGAIN = "Please try again!";

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
}
