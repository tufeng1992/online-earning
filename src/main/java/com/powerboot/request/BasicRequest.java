package com.powerboot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class BasicRequest extends BaseRequest{

    //名字
    @ApiModelProperty("名字")
    private String name;
    //性别 1-男 2-女
    @ApiModelProperty("性别 1-男 2-女")
    private Integer gender;
    //婚姻 1-已婚 2-未婚
    @ApiModelProperty("婚姻 1-单身 2-已婚")
    private Integer marriage;
    //邮箱
    @ApiModelProperty("邮箱")
    private String email;
    //身份证id
    @ApiModelProperty("身份证id")
    private String panId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }
}
