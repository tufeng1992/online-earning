package com.powerboot.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Aspect
@Component
public class ControllerLogAspect {
    protected static Logger logger = LoggerFactory.getLogger(ControllerLogAspect.class);


// 通知（环绕）
    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
//        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        Object[] args = pjp.getArgs();
        String params = "";
        // result的值就是被拦截方法的返回值
        Object result = pjp.proceed();
        try {
            //获取请求参数集合并进行遍历拼接
            if (args.length > 0) {
                if ("POST".equals(method)) {
                    Object object = args[0];
                    if (!(args[0] instanceof HttpServletRequest) && !(args[0] instanceof HttpServletResponse)){
                        params = JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
                    }
                } else if ("GET".equals(method)) {
                    params = queryString;
                }
                if (StringUtils.isEmpty(params)){
                    params="";
                }
                params = URLDecoder.decode(params,UTF_8);
            }
            logger.info("requestMethod:{},url:{},params:{},\n responseBody:{}", method , uri,  params,
                    JSON.toJSONString(result,SerializerFeature.WriteMapNullValue));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("log error !!",e);
        }
        return result;
    }

}