package com.powerboot.response;

import java.util.List;

public class AboutUsResponse {

    private String firstTitle;

    private String secondTitle;

    private List<AbountUsContentResponse> paragraphList;

    private List<String> imageList;

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public List<AbountUsContentResponse> getParagraphList() {
        return paragraphList;
    }

    public void setParagraphList(List<AbountUsContentResponse> paragraphList) {
        this.paragraphList = paragraphList;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
