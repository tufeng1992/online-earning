package com.powerboot.utils;

import com.powerboot.common.JsonUtils;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis操作基础类
 **/
public class RedisUtils {

    private static Logger log = LoggerFactory.getLogger(RedisUtils.class);
    private static StringRedisTemplate stringRedisTemplate;

    static {
        try {
            stringRedisTemplate = SpringContextUtils.getBean(StringRedisTemplate.class);
        } catch (Exception e) {
            log.error("获取StringRedisTemplate异常", e);
        }
    }

    private RedisUtils() {
    }

    /**
     *
     * @param key
     * @param expireTime 超时秒
     * @return
     */
    public static boolean setIfAbsent(String key,int expireTime){
        try {
            if (stringRedisTemplate.opsForValue().setIfAbsent(key,"1")){
                stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                return true;
            }
            return false;
        }catch (Exception e){
            stringRedisTemplate.delete(key);
            return false;
        }
    }

    public static String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public static Integer getInteger(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return StringUtils.isBlank(value) ? null : Integer.parseInt(value);
    }

    public static <T> T getValue(String key, Class<T> cls) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (cls.equals(String.class)) {
            return (T) value;
        }
        if (cls.equals(Integer.class)) {
            Integer iValue = Integer.parseInt(value);
            return (T) iValue;
        } else if (cls.equals(BigDecimal.class)) {
            BigDecimal bValue = new BigDecimal(value);
            return (T) bValue;
        }
        return (T) value;
    }

    public static void setValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public static void setValue(String key, String value, Integer expireTime) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    public static Boolean remove(String key) {
        return stringRedisTemplate.delete(key);
    }

    public static Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public static Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key, 1L);
    }

    public static Long increment(String key, Integer timeout) {
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        return stringRedisTemplate.opsForValue().increment(key, 1L);
    }

    public static Long increment(String key, Long value, Integer expireTime) {
        stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
        return stringRedisTemplate.opsForValue().increment(key, value);
    }

    public static <T> T setIfNotExists(String key, Supplier<T> supplier, Integer timeOut, Class<T> clazz) {
        T resultObj;
        String result = getValue(key, String.class);
        if (StringUtils.isBlank(result)) {
            T supplierResult = supplier.get();
            if (supplierResult != null) {
                setValue(key, JsonUtils.toJSONString(supplierResult), timeOut);
                resultObj = supplierResult;
            } else {
                resultObj = null;
            }
        } else {
            resultObj = JsonUtils.parseObject(result, clazz);
        }
        return resultObj;
    }

    public static <T> List<T> setListIfNotExists(String key, Supplier<List<T>> supplier, Integer timeOut,
        Class<T> clazz) {
        List<T> resultObj;
        String result = getString(key);
        if (StringUtils.isEmpty(result)) {
            List<T> supplierResult = supplier.get();
            if (CollectionUtils.isNotEmpty(supplierResult)) {
                setValue(key, JsonUtils.toJSONString(supplierResult), timeOut);
                resultObj = supplierResult;
            } else {
                resultObj = Collections.emptyList();
            }
        } else {
            resultObj = JsonUtils.parseArray(result, clazz);
        }
        return resultObj;
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        String str = getString(key);
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return JsonUtils.parseArray(str, clazz);
    }

}
