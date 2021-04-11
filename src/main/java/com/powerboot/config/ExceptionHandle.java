package com.powerboot.config;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.common.StringCommonUtils;
import com.powerboot.enums.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ExceptionHandle {

    protected static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    private  ExceptionHandle(){}

    private static StringBuffer sb = new StringBuffer();

    static {
        sb.append("【{}-{}】").append("\n");
        sb.append("【IP】 : {}").append("\n");
        sb.append("【URL】: {}").append("\n");
        sb.append("【请求方式】: {}").append("\n");
        sb.append("【请求参数】: {}").append("\n");
        sb.append("【错误信息】: {}").append("\n");
    }

    @ExceptionHandler(value = Exception.class)
    public BaseResponse exceptionHandler(HttpServletRequest req, Exception ex){
        try {
            logger.error("【{}{}】: URL : {} ERROR : {}", ResultEnum.SYSTEM_EXCEPTION.getCode(),ResultEnum.SYSTEM_EXCEPTION.getMsg(),req.getRequestURL(),ex);
            String errorMsg = StringCommonUtils.buildString(sb.toString(),
                    ResultEnum.SYSTEM_EXCEPTION.getCode(),
                    ResultEnum.SYSTEM_EXCEPTION.getMsg(),
                    getIp(req),
                    req.getRequestURL(),
                    req.getMethod(),
                    req.getParameterMap() == null ? "" : JsonUtils.toJSONString(req.getParameterMap()),
                    ex);
            String title = "----Exception系统异常信息----";
        }catch (Exception e){

        }
        return BaseResponse.fail(ResultEnum.SYSTEM_EXCEPTION.getCode(),ResultEnum.SYSTEM_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse methodArgumentNotValidExceptionHandler(HttpServletRequest req, MethodArgumentNotValidException ex){
        try {
            logger.error("【{}{}】: URL : {} Valid-ERROR : {}", ResultEnum.SYSTEM_EXCEPTION.getCode(),ResultEnum.SYSTEM_EXCEPTION.getMsg(),req.getRequestURL(),ex);
            String errorMsg = StringCommonUtils.buildString(sb.toString(),
                    ResultEnum.SYSTEM_EXCEPTION.getCode(),
                    ResultEnum.SYSTEM_EXCEPTION.getMsg(),
                    getIp(req),
                    req.getRequestURL(),
                    req.getMethod(),
                    req.getParameterMap() == null ? "" : JsonUtils.toJSONString(req.getParameterMap()),
                    ex);
            String title = "----Valid服务异常信息----";
        }catch (Exception e){

        }

        return BaseResponse.fail(ResultEnum.SYSTEM_EXCEPTION.getCode(),ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = BaseException.class)
    public BaseResponse baseExceptionHandler(HttpServletRequest req,BaseException ex){
        try {
            String stackTraceString = printStackTraceToString(ex);
            logger.error(stackTraceString);
            if (ex.getErrorCode().equals("-1")){
                //未登录异常
                return BaseResponse.fail(ex.getErrorCode(),ex.getErrorValue());
            }
            String errorMsg = StringCommonUtils.buildString(sb.toString(),
                    ResultEnum.SYSTEM_EXCEPTION.getCode(),
                    ResultEnum.SYSTEM_EXCEPTION.getMsg(),
                    getIp(req),
                    req.getRequestURL(),
                    req.getMethod(),
                    req.getParameterMap() == null ? "" : JsonUtils.toJSONString(req.getParameterMap()),
                    stackTraceString);
            String title = "----BaseException服务异常信息----";
        }catch (Exception e){

        }
        return BaseResponse.fail(ex.getErrorValue());
    }

    public static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace( new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

    public String getIp(HttpServletRequest request){

        try {
            String ip = request.getHeader("x-forwarded-for");
            String[] array = ip.split(",");
            ip = array[0];
            return ip;
        }catch (Exception e){
            //logger.error("获取唯一ip异常,异常::",e);
        }
        String ip = request.getHeader("X-real-ip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 0 && ip.trim().length() > 0) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
