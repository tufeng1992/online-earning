package com.powerboot.response;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

public class ServiceCenterResponse {

    @ApiModelProperty("顶部提示文案")
    private String top;
    @ApiModelProperty("客服电话")
    private List<ServiceMobileResponse> mobile;
    @ApiModelProperty("客服服务时间")
    private String customerServiceHour;
    @ApiModelProperty("提现工作日提示")
    private String withdrawalTimeDay;
    @ApiModelProperty("提现时间段提示")
    private String withdrawalTimeHour;
    @ApiModelProperty("底部提示文案")
    private String last;

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public List<ServiceMobileResponse> getMobile() {
        return mobile;
    }

    public void setMobile(List<ServiceMobileResponse> mobile) {
        this.mobile = mobile;
    }

    public String getCustomerServiceHour() {
        return customerServiceHour;
    }

    public void setCustomerServiceHour(String customerServiceHour) {
        this.customerServiceHour = customerServiceHour;
    }

    public String getWithdrawalTimeDay() {
        return withdrawalTimeDay;
    }

    public void setWithdrawalTimeDay(String withdrawalTimeDay) {
        this.withdrawalTimeDay = withdrawalTimeDay;
    }

    public String getWithdrawalTimeHour() {
        return withdrawalTimeHour;
    }

    public void setWithdrawalTimeHour(String withdrawalTimeHour) {
        this.withdrawalTimeHour = withdrawalTimeHour;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
