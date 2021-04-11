package com.powerboot.request;

/**
 * Create  2020 - 07 - 25 4:49 下午
 */
public class BackSmsRequest {
//    receiver：接收状态报告验证的用户名（不是账户名），是按照用户要求配置的名称，默认为空
//    pswd：接收状态报告验证的密码，默认为空
//    msgid：提交短信时平台返回的msgid
//    reportTime：网关平台返回的时间,网关不同，格式有偏差，以具体返回格式为准.<br/>
//    notifyTime：接口服务器返回的时间，格式YYMMDDhhmmss，其中YY=年份的最后两位（00-99），MM=月份（01-12），DD=日（01-31），hh=小时（00-23），mm=分钟（00-59）,ss=秒（00-59）。
//    mobile：提交短信时的手机号码
//    status：状态报告状态码
//    batchSeq：短信批次号。

    private String receiver;
    private String pswd;
    private String msgid ;
    private String reportTime;
    private String notifyTime;
    private String mobile;
    private String status;
    private String batchSeq;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatchSeq() {
        return batchSeq;
    }

    public void setBatchSeq(String batchSeq) {
        this.batchSeq = batchSeq;
    }

    @Override
    public String toString() {
        return "BackSmsRequest{" +
                "receiver='" + receiver + '\'' +
                ", pswd='" + pswd + '\'' +
                ", msgid='" + msgid + '\'' +
                ", reportTime='" + reportTime + '\'' +
                ", notifyTime='" + notifyTime + '\'' +
                ", mobile='" + mobile + '\'' +
                ", status='" + status + '\'' +
                ", batchSeq='" + batchSeq + '\'' +
                '}';
    }
}
