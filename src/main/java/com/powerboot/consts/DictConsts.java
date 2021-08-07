package com.powerboot.consts;

import com.powerboot.enums.PaymentChannelEnum;

/**
 * 字典配置
 **/
public class DictConsts {

    //字典缓存存活时间
    public static final Integer DICT_CACHE_LIVE_TIME = 60 * 60 * 24 * 365;
    /**
     * 域名
     */
    public static final String DOMIAN_URL = "DOMIAN_URL:%s";
    /**
     * 风控
     */
    //是否开启IP黑名名单 0-关闭 1-开启
    public static final String OPEN_IP_BLACK_LIST = "OPEN_IP_BLACK_LIST";
    //IP黑名单
    public static final String IP_BLACK_LIST = "IP_BLACK_LIST";
    //黑名单拦截提示
    public static final String RISK_USER_BLACK_TIP = "RISK_USER_BLACK_TIP";
    //用户被禁止登录
    public static final String USER_LOGIN_FLAG = "USER_LOGIN_FLAG:%s";

    /**
     * 跑马灯消息配置
     */
    public static final String APPLY_SHOW_COUNT = "APPLY_SHOW_COUNT";
    public static final String APPLY_AMOUNT_MIN = "APPLY_AMOUNT_MIN";
    public static final String APPLY_AMOUNT_MAX = "APPLY_AMOUNT_MAX";

    /**
     * 登录配置
     */
    //是否开启登录检查 0-关闭 1-开启
    public static final String OPEN_CHECK_LOGIN = "OPEN_CHECK_LOGIN";
    //登录过期时间  单位：小时
    public static final String SSO_LOGIN_EXPIRE = "SSO_LOGIN_EXPIRE";

    /**
     * 验证码配置
     */
    //验证码位数
    public static final String VER_CODE_LENGTH = "VER_CODE_LENGTH";

    //短信重发间隔
    public static final String SMS_RESEND_TIME = "SMS_RESEND_TIME";

    //同一ip指定时间内发送短信最大次数
    public static final String IP_SEND_MAX_COUNT = "IP_SEND_MAX_COUNT";

    //同一手机号指定时间内发送短信最大次数
    public static final String PHONE_SEND_MAX_COUNT = "PHONE_SEND_MAX_COUNT";

    //验证码有效时间
    public static final String VER_CODE_LIVE_TIME = "VER_CODE_LIVE_TIME";

    //同一ip发送短信间隔时间
    public static final String IP_SEND_LIVE_TIME = "IP_SEND_LIVE_TIME";

    //同一手机号发送短信间隔时间
    public static final String PHONE_SEND_LIVE_TIME = "PHONE_SEND_LIVE_TIME";

    //成就文案
    public static final String MISSION_TOP_TIPS = "MISSION_TOP_TIPS";

    //成就任务奖励基数
    public static final String MISSION_TASK_AMOUNT = "MISSION_TASK_AMOUNT";

    /**
     * 提现配置
     */
    //08:45
    public static final String WITHDRAW_START_TIME = "WITHDRAW_START_TIME";
    //18:15
    public static final String WITHDRAW_END_TIME = "WITHDRAW_END_TIME";
    //Sorry, according to the bank regulations, the withdrawal time is from 9:00 to 18:00 on working days!
    public static final String WITHDRAW_TIME_TIP = "WITHDRAW_TIME_TIP";
    //The payment channel is being upgraded, please wait for one hour.
    public static final String WITHDRAW_USER_CLOSE_TIP = "WITHDRAW_USER_CLOSE_TIP";

    /**
     * 支付配置
     */
    //首冲上级返现金额
    public static final String FIRST_RECHARGE_PARENT_AMOUNT = "FIRST_RECHARGE_PARENT_AMOUNT";

    //首冲上级返现金额
    public static final String FIRST_RECHARGE_PARENT_AMOUNT_LEVEL2 = "FIRST_RECHARGE_PARENT_AMOUNT_LEVEL2";

    //首冲上级返现金额
    public static final String FIRST_RECHARGE_PARENT_AMOUNT_LEVEL3 = "FIRST_RECHARGE_PARENT_AMOUNT_LEVEL3";

    //首次登陆成功返现
    public static final String FIRST_LOGIN_PARENT_AMOUNT = "FIRST_LOGIN_PARENT_AMOUNT";


    //充值金额集合
    public static final String RECHARGE_AMOUNT_LIST = "RECHARGE_AMOUNT_LIST";

    //充值输入框文案颜色
    public static final String RECHARGE_AMOUNT_TEXT_COLOR = "RECHARGE_AMOUNT_TEXT_COLOR";

    //充值提示语
    public static final String RECHARGE_AMOUNT_TIPS = "RECHARGE_AMOUNT_TIPS";

    //提现最低金额
    public static final String WITHDRAW_AMOUNT_MIN = "WITHDRAW_AMOUNT_MIN";

    //提现输入框文案颜色
    public static final String WITHDRAW_AMOUNT_TEXT_COLOR = "WITHDRAW_AMOUNT_TEXT_COLOR";

    //提现提示语
    public static final String WITHDRAW_AMOUNT_TIPS = "WITHDRAW_AMOUNT_TIPS";

    //跑马灯文案颜色
    public static final String HOME_TEXT_COLOR = "HOME_TEXT_COLOR";

    //注册送余额开关 0-关 1-开
    public static final String REG_GIVE_MONEY_SWITCH = "REG_GIVE_MONEY_SWITCH";

    //注册送余额金额
    public static final String REG_GIVE_MONEY = "REG_GIVE_MONEY";

    //支付开关1-开,0-关
    public static final String PAY_SWITCH = "PAY_SWITCH";

    //支付赠送余额开关
    public static final String PAY_SEND_BALANCE_SWITCH = "PAY_SEND_BALANCE_SWITCH";

    //支付赠送余额比例
    public static final String PAY_SEND_BALANCE_RATIO = "PAY_SEND_BALANCE_RATIO";

    //系统支付关闭通知文案
    public static final String PAY_CLOSE_CONTENT = "PAY_CLOSE_CONTENT";

    //个人支付关闭通知文案
    public static final String PAY_CLOSE_USER_CONTENT = "PAY_CLOSE_USER_CONTENT";

    //封盘开关 0-关 1-开
    public static final String GO_PAY_SWITCH = "GO_PAY_SWITCH";

    //封盘开始时间
    public static final String GO_PAY_DATE = "GO_PAY_DATE";

    //封盘提示文案
    public static final String GO_PAY_TIP = "GO_PAY_TIP";

    //支付key_id
    public static final String PAY_KEY_ID = "PAY_KEY_ID";

    //支付key_secret
    public static final String PAY_KEY_SECRET = "PAY_KEY_SECRET";

    //razorpay卡号
    public static final String PAY_ACCOUNT_NUMBER = "PAY_ACCOUNT_NUMBER";

    //支付回调PAY_SECRET
    public static final String BACK_PAY_SECRET = "BACK_PAY_SECRET";

    //印度手机正则表达式
    public static final String INDIA_PATTERN = "INDIA_PATTERN";

    //白名单手机号
    public static final String WHITE_PHONE = "WHITE_PHONE";

    //白名单设备号
    public static final String WHITE_DEVICE = "WHITE_DEVICE";

    //白名单ip
    public static final String WHITE_IP = "WHITE_IP";


    //短信请求服务url
    public static final String SMS_SINGLE_REQUEST_SERVER_URL = "SMS_SINGLE_REQUEST_SERVER_URL";
    //短信charset
    public static final String SMS_CHARSET = "SMS_CHARSET";
    //api账号
    public static final String SMS_ACCOUNT = "SMS_ACCOUNT";
    //api密码
    public static final String SMS_PASSWORD = "SMS_PASSWORD";

    //APP版本
    public static final String APP_VERSION = "APP_VERSION";

    //APP更新URL
    public static final String APP_UPDATE_URL = "APP_UPDATE_URL";
    //不需要强制更新版本 多个,分隔
    public static final String APP_IGNORE_FORCE_UPDATE = "APP_IGNORE_FORCE_UPDATE";

    //提现服务费比例
    public static final String SERVICE_RATIO = "SERVICE_RATIO";

    //用户每日最大充值次数
    public static final String TODAY_MAX_RECHARGE_COUNT = "TODAY_MAX_RECHARGE_COUNT";

    //VIP? 单次提现最大限额
    public static final String VIP_SINGLE_MAX_WITHDRAW_QUOTA = "VIP%s_SINGLE_MAX_WITHDRAW_QUOTA";
    //VIP? 每日提现最大次数限制
    public static final String VIP_TODAY_MAX_WITHDRAW_COUNT = "VIP%s_TODAY_MAX_WITHDRAW_COUNT";


    //VIP1 单次提现最大限额
    public static final String VIP1_SINGLE_MAX_WITHDRAW_QUOTA = "VIP1_SINGLE_MAX_WITHDRAW_QUOTA";
    //VIP2 单次提现最大限额
    public static final String VIP2_SINGLE_MAX_WITHDRAW_QUOTA = "VIP2_SINGLE_MAX_WITHDRAW_QUOTA";
    //VIP3 单次提现最大限额
    public static final String VIP3_SINGLE_MAX_WITHDRAW_QUOTA = "VIP3_SINGLE_MAX_WITHDRAW_QUOTA";
    //VIP4 单次提现最大限额
    public static final String VIP4_SINGLE_MAX_WITHDRAW_QUOTA = "VIP4_SINGLE_MAX_WITHDRAW_QUOTA";
    //VIP1 每日提现最大次数限制
    public static final String VIP1_TODAY_MAX_WITHDRAW_COUNT = "VIP1_TODAY_MAX_WITHDRAW_COUNT";
    //VIP2 每日提现最大次数限制
    public static final String VIP2_TODAY_MAX_WITHDRAW_COUNT = "VIP2_TODAY_MAX_WITHDRAW_COUNT";
    //VIP3 每日提现最大次数限制
    public static final String VIP3_TODAY_MAX_WITHDRAW_COUNT = "VIP3_TODAY_MAX_WITHDRAW_COUNT";
    //VIP4 每日提现最大次数限制
    public static final String VIP4_TODAY_MAX_WITHDRAW_COUNT = "VIP4_TODAY_MAX_WITHDRAW_COUNT";
    //系统每日最大提现限额
    public static final String SYS_TODAY_MAX_WITHDRAW_QUOTA = "SYS_TODAY_MAX_WITHDRAW_QUOTA";
    //单笔最低提现限额
    public static final String SINGE_LOW_WITHDRAW_QUOTA = "SINGE_LOW_WITHDRAW_QUOTA";
    //系统提现开关0-关(不允许提现) 1-开(允许提现)
    public static final String SYS_WITHDRAWAL_CHECK = "SYS_WITHDRAWAL_CHECK";
    //系统体现提示语
    public static final String SYS_WITHDRAWAL_CHECK_TIPS = "SYS_WITHDRAWAL_CHECK_TIPS";


    //wegame 商户号
    public static final String WE_GAME_NUMBER = "WE_GAME_NUMBER";
    //wegame API KEY
    public static final String WE_GAME_API_KEY = "WE_GAME_API_KEY";

    /**
     * {@link PaymentChannelEnum}
     * 支付渠道 1-跳转支付链接 2-三方rzp
     */
    public static final String PAY_CHANNEL = "PAY_CHANNEL";

    public static final String PAY_CHANNEL_BRANCH = "PAY_CHANNEL_BRANCH";

    //支付渠道
    public static final String PAYOUT_CHANNEL = "PAYOUT_CHANNEL";
    //支付渠道分支 1-wegame 2-paystax
    public static final String PAYOUT_CHANNEL_BRANCH = "PAYOUT_CHANNEL_BRANCH";

    //wegame 支付类型
    public static final String WE_GAME_PAY_TYPE = "WE_GAME_PAY_TYPE";

    //wegame baseURL
    public static final String WE_GAME_BASE_URL = "WE_GAME_BASE_URL";
    //wegame 支付回调url
    public static final String WE_GAME_PAY_URL = "WE_GAME_PAY_URL";

    //wegame 代付回调url
    public static final String WE_GAME_PAYOUT_URL = "WE_GAME_PAYOUT_URL";

    //payStack baseURL
    public static final String PAY_STACK_BASE_URL = "PAY_STACK_BASE_URL";

    //payStack 支付类型
    public static final String PAY_STACK_PAY_TYPE = "PAY_STACK_PAY_TYPE";

    /**
     * payStack 支付回调
     */
    public static final String PAY_STACK_PAY_CALL_BACK_URL = "PAY_STACK_PAY_CALL_BACK_URL";

    /**
     * payStack 公钥
     */
    public static final String PAY_STACK_PK = "PAY_STACK_PK";

    /**
     * payStack 私钥
     */
    public static final String PAY_STACK_SK = "PAY_STACK_SK";

    //payStack 商户号
    public static final String PAY_STACK_MERCHANT = "PAY_STACK_MERCHANT";

    //payStack 代付回调url
    public static final String PAY_STACK_PAYOUT_URL = "PAY_STACK_PAYOUT_URL";

    /**
     * payStack 银行列表
     */
    public static final String PAY_STACK_BANK_LIST = "PAY_STACK_BANK_LIST";

    /**
     * OPay 支付回调
     */
    public static final String OPAY_PAY_CALL_BACK_URL = "OPAY_PAY_CALL_BACK_URL";

    /**
     * opay商户id
     */
    public static final String OPAY_MERCHANTID = "OPAY_MERCHANTID";

    /**
     * opay私钥
     */
    public static final String OPAY_SECURT_KEY = "OPAY_SECURT_KEY";

    /**
     * opay公钥
     */
    public static final String OPAY_PUBLIC_KEY = "OPAY_PUBLIC_KEY";

    /**
     * opay网关地址
     */
    public static final String OPAY_GATEWAY_URL = "OPAY_GATEWAY_URL";

    /**
     * flutter私钥
     */
    public static final String FLUTTER_SK = "FLUTTER_SK";

    /**
     * flutter公钥
     */
    public static final String FLUTTER_PK = "FLUTTER_PK";

    /**
     * flutter加密key
     */
    public static final String FLUTTER_ENCRYPTION_KEY = "FLUTTER_ENCRYPTION_KEY";

    /**
     * flutter网关地址
     */
    public static final String FLUTTER_GATEWAY_URL = "FLUTTER_GATEWAY_URL";

    /**
     * flutter redirect地址
     */
    public static final String FLUTTER_REDIRECT_URL = "FLUTTER_REDIRECT_URL";

    /**
     * flutter callback地址
     */
    public static final String FLUTTER_CALLBACK_URL = "FLUTTER_CALLBACK_URL";

    /**
     * flutter支付自定义title
     */
    public static final String FLUTTER_CUSTOMER_TITLE = "FLUTTER_CUSTOMER_TITLE";

    /**
     * flutter支付自定义des
     */
    public static final String FLUTTER_CUSTOMER_DES = "FLUTTER_CUSTOMER_DES";

    /**
     * flutter支付自定义logo
     */
    public static final String FLUTTER_CUSTOMER_LOGO = "FLUTTER_CUSTOMER_LOGO";

    /**
     * flutter支付webhook hash
     */
    public static final String FLUTTER_WEBHOOK_HASH = "FLUTTER_WEBHOOK_HASH";

    /**
     * wallyt网关地址
     */
    public static final String WALLYT_GATEWAY_URL = "WALLYT_GATEWAY_URL";

    /**
     * wallyt partnerId
     */
    public static final String WALLYT_PARTNER_ID = "WALLYT_PARTNER_ID";

    /**
     * wallyt 签名私钥
     */
    public static final String WALLYT_SIGN_PRIK = "WALLYT_SIGN_PRIK";

    /**
     * wallyt 签名公钥
     */
    public static final String WALLYT_SIGN_PUBK = "WALLYT_SIGN_PUBK";

    /**
     * wallyt 转账回调通知url
     */
    public static final String WALLYT_PAYOUT_NOTIFY_URL = "WALLYT_PAYOUT_NOTIFY_URL";

    /**
     * 商户id
     */
    public static final String GRECASH_MERCHANT_ID = "GRECASH_MERCHANT_ID";

    /**
     * grecash app id
     */
    public static final String GRECASH_APP_ID = "GRECASH_APP_ID";

    /**
     * grecash app secret
     */
    public static final String GRECASH_APP_SECRET = "GRECASH_APP_SECRET";

    /**
     * access token
     */
    public static final String GRECASH_ACCESS_TOKEN = "GRECASH_ACCESS_TOKEN";

    /**
     * 基础网关
     */
    public static final String GRECASH_BASE_URL = "GRECASH_BASE_URL";

    /**
     * 支付回调地址
     */
    public static final String GRECASH_PAY_NOTIFY_URL = "GRECASH_PAY_NOTIFY_URL";

    /**
     * 转账回调地址
     */
    public static final String GRECASH_PAY_OUT_NOTIFY_URL = "GRECASH_PAY_OUT_NOTIFY_URL";

    /**
     * 支付重定向地址
     */
    public static final String GRECASH_PAY_REDIRECT_URL = "GRECASH_PAY_REDIRECT_URL";

    /**
     * 商户id
     */
    public static final String GMS_MERCHANT_ID = "GMS_MERCHANT_ID";

    /**
     * 基础网关
     */
    public static final String GMS_BASE_URL = "GMS_BASE_URL";


    /**
     * 支付回调地址
     */
    public static final String GMS_PAY_NOTIFY_URL = "GMS_PAY_NOTIFY_URL";

    /**
     * 支付重定向地址
     */
    public static final String GMS_PAY_REDIRECT_URL = "GMS_PAY_REDIRECT_URL";

    /**
     * 支付回调地址
     */
    public static final String GMS_PAY_OUT_NOTIFY_URL = "GMS_PAY_OUT_NOTIFY_URL";

    /**
     * 支付key
     */
    public static final String GMS_PAY_IN_KEY = "GMS_PAY_IN_KEY";

    /**
     * 代付key
     */
    public static final String GMS_PAY_OUT_KEY = "GMS_PAY_OUT_KEY";

    /**
     * 默认支付类型
     */
    public static final String GMS_DEFAULT_PAY_TYPE = "GMS_DEFAULT_PAY_TYPE";

    /**
     * 商户id
     */
    public static final String SEPRO_MERCHANT_ID = "SEPRO_MERCHANT_ID";

    /**
     * 基础网关
     */
    public static final String SEPRO_BASE_URL = "SEPRO_BASE_URL";


    /**
     * 支付回调地址
     */
    public static final String SEPRO_PAY_NOTIFY_URL = "SEPRO_PAY_NOTIFY_URL";

    /**
     * 支付重定向地址
     */
    public static final String SEPRO_PAY_REDIRECT_URL = "SEPRO_PAY_REDIRECT_URL";

    /**
     * 支付回调地址
     */
    public static final String SEPRO_PAY_OUT_NOTIFY_URL = "SEPRO_PAY_OUT_NOTIFY_URL";

    /**
     * 支付key
     */
    public static final String SEPRO_PAY_IN_KEY = "SEPRO_PAY_IN_KEY";

    /**
     * 代付key
     */
    public static final String SEPRO_PAY_OUT_KEY = "SEPRO_PAY_OUT_KEY";

    /**
     * 默认支付类型
     */
    public static final String SEPRO_DEFAULT_PAY_TYPE = "SEPRO_DEFAULT_PAY_TYPE";


    /**
     * 商户id
     */
    public static final String THKINGZ_MERCHANT_ID = "THKINGZ_MERCHANT_ID";

    /**
     * 基础网关
     */
    public static final String THKINGZ_BASE_URL = "THKINGZ_BASE_URL";


    /**
     * 支付回调地址
     */
    public static final String THKINGZ_PAY_NOTIFY_URL = "THKINGZ_PAY_NOTIFY_URL";

    /**
     * 支付重定向地址
     */
    public static final String THKINGZ_PAY_ERROR_REDIRECT_URL = "THKINGZ_PAY_ERROR_REDIRECT_URL";

    /**
     * 支付重定向地址
     */
    public static final String THKINGZ_PAY_REDIRECT_URL = "THKINGZ_PAY_REDIRECT_URL";

    /**
     * 支付回调地址
     */
    public static final String THKINGZ_PAY_OUT_NOTIFY_URL = "THKINGZ_PAY_OUT_NOTIFY_URL";

    /**
     * 支付key
     */
    public static final String THKINGZ_PAY_IN_KEY = "THKINGZ_PAY_IN_KEY";

    /**
     * 默认支付类型
     */
    public static final String THKINGZ_DEFAULT_PAY_TYPE = "THKINGZ_DEFAULT_PAY_TYPE";

    //相同ip注册最大个数
    public static final String IP_REGISTER_MAX_COUNT = "IP_REGISTER_MAX_COUNT";

    //钱包警告金额
    public static final String WALLET_WARNING_AMOUNT = "WALLET_WARNING_AMOUNT";

    //提现警告金额
    public static final String WITHDRAW_WARNING_AMOUNT = "WITHDRAW_WARNING_AMOUNT";

    //PAYSTAX baseURL
    public static final String PAYSTAX_BASE_URL = "PAYSTAX_BASE_URL";

    //PAYSTAX 支付回调url
    public static final String PAYSTAX_PAY_URL = "PAYSTAX_PAY_URL";

    //PAYSTAX 代付回调url
    public static final String PAYSTAX_PAYOUT_URL = "PAYSTAX_PAYOUT_URL";

    //PAYSTAX Merchant SN
    public static final String PAYSTAX_MERCHANT_SN = "PAYSTAX_MERCHANT_SN";
    //PAYSTAX Access Key ( AK )
    public static final String PAYSTAX_ACCESS_KEY = "PAYSTAX_ACCESS_KEY";
    //PAYSTAX Secret Key ( SK )
    public static final String PAYSTAX_SECRET_KEY = "PAYSTAX_SECRET_KEY";


    //审批开关 0-关 1-开
    public static final String APPLY_SWITCH = "APPLY_SWITCH";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_FIRST_TITLE = "ABOUNT_FIRST_TITLE";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_SECOND_TITLE = "ABOUNT_SECOND_TITLE";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_A1_TIP = "ABOUNT_A1_TIP";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_C1 = "ABOUNT_C1";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_A2_TIP = "ABOUNT_A2_TIP";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_C2 = "ABOUNT_C2";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_A3_TIP = "ABOUNT_A3_TIP";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_C3 = "ABOUNT_C3";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_C3_2 = "ABOUNT_C3_2";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_C3_3 = "ABOUNT_C3_3";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_IMG1 = "ABOUNT_IMG1";

    /**
     * 关于我配置
     */
    public static final String ABOUNT_IMG2 = "ABOUNT_IMG2";

    /**
     * 商品等级金额配置
     */
    public static final String PRODUCT_DESC_AMOUNT = "PRODUCT_DESC_AMOUNT";

    /**
     * 商品等级描述配置
     */
    public static final String PRODUCT_INTRODUCTION = "PRODUCT_INTRODUCTION";

    /**
     * 创蓝短信发送地址
     */
    public static final String CL_SMS_SEND_GJ_URL = "CL_SMS_SEND_GJ_URL";

    /**
     * 创蓝短信发送账号
     */
    public static final String CL_SMS_SEND_GJ_ACCOUNT = "CL_SMS_SEND_GJ_ACCOUNT";

    /**
     * 创蓝短信发送密码
     */
    public static final String CL_SMS_SEND_GJ_PASSWORD = "CL_SMS_SEND_GJ_PASSWORD";

    /**
     * 修改密码开关
     */
    public static final String MODIFY_PASSWORD_SWITCH = "MODIFY_PASSWORD_SWITCH";

    /**
     * 发送短信开关
     */
    public static final String SEND_SMS_SWITCH = "SEND_SMS_SWITCH";

    /**
     * 注册发送短信开关
     */
    public static final String SEND_REGISTER_SMS_SWITCH = "SEND_REGISTER_SMS_SWITCH";

    /**
     * 提现校验任务开关
     */
    public static final String PAY_OUT_CHECK_JOB_SWITCH = "PAY_OUT_CHECK_JOB_SWITCH";

    /**
     * 支付校验任务开关
     */
    public static final String PAY_IN_CHECK_JOB_SWITCH = "PAY_IN_CHECK_JOB_SWITCH";

    /**
     * infobip api key
     */
    public static final String INFOBIP_VOICE_MESSAGE_API_KEY = "INFOBIP_VOICE_MESSAGE_API_KEY";

    /**
     * infobip base url
     */
    public static final String INFOBIP_VOICE_MESSAGE_BASE_URL = "INFOBIP_VOICE_MESSAGE_BASE_URL";

    /**
     * infobip from
     */
    public static final String INFOBIP_VOICE_MESSAGE_FROM = "INFOBIP_VOICE_MESSAGE_FROM";

    /**
     * 不允许提现日  with week
     */
    public static final String UN_WITHDRAW_DAYS = "UN_WITHDRAW_DAYS";

    /**
     * 刷单限额错误
     */
    public static final String ORDER_PURCHASE_LIMIT_FAIL = "ORDER_PURCHASE_LIMIT_FAIL";

    /**
     * 购买vip子用户数量限制开关
     */
    public static final String CREATE_VIP_CHILD_LIMIT_SWITCH = "CREATE_VIP_CHILD_LIMIT_SWITCH";

    /**
     * 刷单子用户数量限制开关
     */
    public static final String CREATE_ORDER_PURCHASE_CHILD_LIMIT_SWITCH = "CREATE_ORDER_PURCHASE_CHILD_LIMIT_SWITCH";

    /**
     * 第一次登陆上级返现开关
     */
    public static final String FIRST_LOGIN_PARENT_SWITCH = "FIRST_LOGIN_PARENT_SWITCH";

    /**
     * 默认语言
     */
    public static final String DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE";

    /**
     * 服务地区
     */
    public static final String SERVER_AREA = "SERVER_AREA";

    /**
     * 发送验证码类型：sms、voice
     */
    public static final String SEND_SMS_TYPE = "SEND_SMS_TYPE";

    /**
     * VIP1 图片url
     */
    public static final String VIP1_IMAGE_PIC_URL = "VIP1_IMAGE_PIC_URL";
    /**
     * VIP2 图片url
     */
    public static final String VIP2_IMAGE_PIC_URL = "VIP2_IMAGE_PIC_URL";
    /**
     * VIP3 图片url
     */
    public static final String VIP3_IMAGE_PIC_URL = "VIP3_IMAGE_PIC_URL";
    /**
     * VIP4 图片url
     */
    public static final String VIP4_IMAGE_PIC_URL = "VIP4_IMAGE_PIC_URL";

    /**
     * 用户登录设备限制数量
     */
    public static final String USER_LOGIN_DEVICE_LIMIT_COUNT = "USER_LOGIN_DEVICE_LIMIT_COUNT";

    /**
     * 用户登录ip限制数量
     */
    public static final String USER_LOGIN_IP_LIMIT_COUNT = "USER_LOGIN_IP_LIMIT_COUNT";

    /**
     * 邀请奖励频率
     */
    public static final String INVITE_FREQUENCY_LIMIT = "INVITE_FREQUENCY_LIMIT";

    /**
     * 邀请奖励次数限制
     */
    public static final String INVITE_REWARD_COUNT_LIMIT = "INVITE_REWARD_COUNT_LIMIT";

    /**
     * 邀请奖励金额
     */
    public static final String INVITE_REWARD_AMOUNT = "INVITE_REWARD_AMOUNT";

    /**
     * adjust事件埋点授权token
     */
    public static final String ADJUST_EVENT_AUTH = "ADJUST_EVENT_AUTH";

    /**
     * adjust事件埋点基础url
     */
    public static final String ADJUST_EVENT_URL = "ADJUST_EVENT_URL";
    /**
     * adjust事件埋点app token
     */
    public static final String ADJUST_EVENT_APP_TOKEN = "ADJUST_EVENT_APP_TOKEN";

    /**
     * adjust事件埋点 充值成功事件名称
     */
    public static final String ADJUST_EVENT_RECHARGE_SUCCESS = "ADJUST_EVENT_RECHARGE_SUCCESS";

    /**
     * adjust事件埋点 购买vip成功事件名称
     */
    public static final String ADJUST_EVENT_VIP_SUCCESS = "ADJUST_EVENT_VIP_SUCCESS";

    /**
     * qeapay地址
     */
    public static final String QEAPAY_BASE_URL = "QEAPAY_BASE_URL";

    /**
     * qeapay商户id
     */
    public static final String QEAPAY_MERCHANT_ID = "QEAPAY_MERCHANT_ID";

    /**
     * qeapay支付回调地址
     */
    public static final String QEAPAY_PAY_NOTIFY_URL = "QEAPAY_PAY_NOTIFY_URL";

    /**
     * qeapay代付回调地址
     */
    public static final String QEAPAY_PAY_OUT_NOTIFY_URL = "QEAPAY_PAY_OUT_NOTIFY_URL";

    /**
     * qeapay支付重定向地址
     */
    public static final String QEAPAY_PAY_REDIRECT_URL = "QEAPAY_PAY_REDIRECT_URL";

    /**
     * 支付key
     */
    public static final String QEAPAY_PAY_IN_KEY = "QEAPAY_PAY_IN_KEY";

    /**
     * 代付key
     */
    public static final String QEAPAY_PAY_OUT_KEY = "QEAPAY_PAY_OUT_KEY";

    /**
     * 首充奖励比例
     */
    public static final String PRIZE_RATIO_CHARGE = "PRIZE_RATIO_CHARGE";

    /**
     * 邀请好友奖励比例
     */
    public static final String PRIZE_RATIO_INVITE = "PRIZE_RATIO_INVITE";

    /**
     * 完成任务奖励比例
     */
    public static final String PRIZE_RATIO_MISSION = "PRIZE_RATIO_MISSION";
}
