package com.powerboot.base;

import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 */
@ApiModel(value = "返回基类")
public class BaseResponse<T> implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(BaseResponse.class);
    private static final String successCode = "200";
    private static final String successMsg = "success";
    private static final String failCode = "500";
    private static final String failMsg = "error";
    @ApiModelProperty(value = "状态,-1:未登录")
    private String state;
    @ApiModelProperty(value = "信息")
    private String msg;
    //app版本号
    @ApiModelProperty(value = "app版本号 如果版本号和当前不一样则显示下载")
    private String appVersion;
    //app下载地址
    @ApiModelProperty(value = "app下载地址")
    private String appDownLoadURL;
    @ApiModelProperty(value = "不需要强制更新版本 多个,分隔")
    private String appIgnoreForceUpdate;
    @ApiModelProperty(value = "包装类")
    private T resultData;
    @ApiModelProperty(value = "是否成功")
    private boolean isSuccess;
    @ApiModelProperty(value = "是否失败")
    private boolean isFail;

    public BaseResponse(String state,String msg,T resultData,boolean isSuccess,boolean isFail){
        this.state = state;
        this.msg = msg;
        this.resultData = resultData;
        this.isSuccess = isSuccess;
        this.isFail = isFail;
        this.appVersion= RedisUtils.getValue(DictConsts.APP_VERSION,String.class);
        this.appDownLoadURL= RedisUtils.getValue(DictConsts.APP_UPDATE_URL,String.class);
        this.appIgnoreForceUpdate= RedisUtils.getValue(DictConsts.APP_IGNORE_FORCE_UPDATE,String.class);
    }
    public BaseResponse(){

    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppDownLoadURL() {
        return appDownLoadURL;
    }

    public String getAppIgnoreForceUpdate() {
        return appIgnoreForceUpdate;
    }

    public static <T>BaseResponse<T> success(){
        return success(null);
    }

    public static <T>BaseResponse<T> success(T resultData){
        return success(successMsg,resultData);
    }

    public static <T>BaseResponse<T> success(String msg,T resultData){
        return new BaseResponse(successCode,msg,resultData,true,false);
    }


    public static <T>BaseResponse<T> fail(){
        return fail(failMsg);
    }

    public static <T>BaseResponse<T> fail(String msg){
        return fail(failCode,msg);
    }

    public static <T>BaseResponse<T> fail(String state,String msg){
        logger.error(msg);
        return new BaseResponse(state,msg,null,false,true);
    }
}
