package com.powerboot.config;

import com.powerboot.common.StringCommonUtils;
import com.powerboot.enums.ResultEnum;


public class BaseException extends RuntimeException{
    private final static long serialVersionUID = 6476473452739657628L;

    private String[] errorValues;
    private String errorValue = ResultEnum.UNKNOWN_EXCEPTION.getMsg();
    private String errorCode = ResultEnum.UNKNOWN_EXCEPTION.getCode();

    public BaseException() {
        super();
    }

    public BaseException(String errorValue) {
        super(errorValue);
        this.errorValue = errorValue;
    }

    public BaseException(String errorCode, String errorValue) {
        super(errorCode +" : "+errorValue);
        this.errorCode = errorCode;
        this.errorValue = errorValue;
    }

    public BaseException(String format, Object errorValues) {
        super(formatMsg(format,errorValues.toString()));
        this.errorValue = errorValues.toString();
    }

    public BaseException(String format, String... errorValues) {
        super(formatMsg(format,errorValues));
        this.errorValues = errorValues;
    }

    public BaseException(String errorCode, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.errorValue = errorCode + ":" +cause;
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String format,
                        Throwable cause,String... errorValues) {
        super(formatMsg(format,errorValues), cause);
        this.errorValues=errorValues;
        this.errorValue = formatMsg(format,errorValues);
    }


    public BaseException(String errorCode, String errorValue,
                        Throwable cause) {
        super(errorCode +" : "+errorValue, cause);
        this.errorCode = errorCode;
        this.errorValue=errorValue;
    }

    private static String formatMsg(String format,Object... args){
        String result = "";
        try {
            result = StringCommonUtils.buildString(format,args);
        }catch (Exception e){
            result = format;
            if (args != null){
                for (Object arg:args) {
                    if (arg != null){
                        result += arg;
                    }
                }
            }
        }
        return result;
    }


    public String getErrorValue() {
        return errorValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getOrgMessage() {
        return super.getMessage();
    }
}
