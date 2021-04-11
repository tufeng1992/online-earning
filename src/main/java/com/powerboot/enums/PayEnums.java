package com.powerboot.enums;

/**
 * Create  2020 - 07 - 17 6:05 下午
 */
public class PayEnums {

    /**
     * 系统提现开关
     */
    public enum SysWithdrawalCheckEnum {

        OPEN(1,"open"),
        CLOSE(0,"close");

        private final Integer code;
        private final String msg;

        SysWithdrawalCheckEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 个人提现开关
     */
    public enum WithdrawalCheckEnum {

        OPEN(1,"open"),
        CLOSE(0,"close");

        private final Integer code;
        private final String msg;

        WithdrawalCheckEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 充值系统开关
     */
    public enum PaySwitchEnum {

        OPEN(1,"open"),
        CLOSE(0,"close");

        private final Integer code;
        private final String msg;

        PaySwitchEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 个人充值开关
     */
    public enum RechargeCheckEnum {

        OPEN(1,"open"),
        CLOSE(0,"close");

        private final Integer code;
        private final String msg;

        RechargeCheckEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    /**
     * 收盘开关
     */
    public enum GoPaySwitchEnum {

        OPEN(1,"open"),
        CLOSE(0,"close");

        private final Integer code;
        private final String msg;

        GoPaySwitchEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 支付类型
     */
    public enum PayTypeEnum {

        RECHARGE(1,"充值"),
        PAY_VIP2(2,"购买VIP2"),
        PAY_VIP3(3,"购买VIP3"),
        PAY_VIP4(4,"购买VIP4"),
        PAY_VIP5(5,"购买VIP5"),
        WITHDRAW(99,"提现"),
        ;

        private final Integer code;
        private final String msg;

        PayTypeEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public static String getDescByCode(Integer code){
            PayTypeEnum[] values = values();
            for (PayTypeEnum singEnum: values) {
                if (singEnum.getCode().equals(code)){
                    return singEnum.getMsg();
                }
            }
            return "";
        }
        public static String getENDescByCode(Integer code){
            PayTypeEnum[] values = values();
            for (PayTypeEnum singEnum: values) {
                if (singEnum.getCode().equals(code)){
                    return singEnum.name();
                }
            }
            return "";
        }
    }

    /**
     * 支付状态
     */
    public enum PayStatusEnum {

        TIMEOUT(-1,"超时未支付"),
        PAYING(1,"支付中"),
        PAID(2,"已支付"),
        FAIL(3,"支付失败");

        private final Integer code;
        private final String msg;

        PayStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
        public static String getDescByCode(Integer code){
            PayStatusEnum[] values = values();
            for (PayStatusEnum singEnum: values) {
                if (singEnum.getCode().equals(code)){
                    return singEnum.getMsg();
                }
            }
            return "";
        }
    }
    /**
     * 审批状态
     */
    public enum PayApplyStatusEnum {

        APPLY(1,"审批中"),
        PASS(2,"审批通过"),
        REJECT(99,"审批驳回"),;

        private final Integer code;
        private final String msg;

        PayApplyStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
        public static String getDescByCode(Integer code){
            PayApplyStatusEnum[] values = values();
            for (PayApplyStatusEnum singEnum: values) {
                if (singEnum.getCode().equals(code)){
                    return singEnum.getMsg();
                }
            }
            return "";
        }
    }
    public enum RazorpayStatusEnum {

        CREATED("created","创建订单"),
        ATTEMPTED("attempted","付款中"),
        PAID("paid","付款成功");

        private final String code;
        private final String msg;

        RazorpayStatusEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
