package com.powerboot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {

    protected static Logger log = LoggerFactory.getLogger(EhCacheUtil.class);
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
        if (applicationContext == null) {
            log.warn("[SpringContextUtil]applicationContext set null");
        } else {
            log.info("[SpringContextUtil]applicationContext has been set...");
        }
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            log.warn("[SpringContextUtil] getBean but applicationContext is null");
        }
        return (T) applicationContext.getBean(clazz);
    }
}
