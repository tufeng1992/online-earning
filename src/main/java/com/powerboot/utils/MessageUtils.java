package com.powerboot.utils;

import com.powerboot.consts.DictConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 获取i18n资源文件
 */
@Slf4j
public class MessageUtils
{
    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public static String message(String code, Object... args)
    {
        MessageSource messageSource = SpringContextUtils.getBean(MessageSource.class);
        String language = RedisUtils.getString(DictConsts.DEFAULT_LANGUAGE);
        Locale locale = new Locale(language);
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            language = request.getHeader("language");
            if (StringUtils.isNotBlank(language)) {
                locale = new Locale(language);
            }
        } catch (Exception e) {
//            log.warn("error for getMessage", e);
        }
        return messageSource.getMessage(code, args, locale);
    }
}
