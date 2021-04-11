package com.powerboot.response;

import java.util.List;

public class ServiceMobileResponse {

    private String mobileName;

    private List<String> mobileNumber;

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public List<String> getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(List<String> mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
