package com.powerboot.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {

    protected static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static <T> T fromJson(String json, Class<T> cls) {
        return new GsonBuilder().create().fromJson(json, cls);
    }

    public static String toStringNoSerializer(Object o) {
        return JSONObject.toJSONString(o);
    }

    public static String toJSONString(Object o) {
        return JSONObject.toJSONString(o, SerializerFeature.WriteNonStringValueAsString);
    }

    public static String toJSONStringWithDateFormat(Object o, String format) {
        return JSONObject.toJSONStringWithDateFormat(o, format,new SerializerFeature[]{
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNonStringValueAsString,
                SerializerFeature.WriteNullStringAsEmpty//String为null时输出""
        });
    }

    public static String getAsString(String data,String field){
        return getFieldStr(parseJsonObject(data),field);
    }

    public static JsonObject parseJsonObject(String data){
        return (JsonObject) new JsonParser().parse(data);
    }

    public static String getFieldStr(JsonObject obj, String field) {
        return (obj.get(field) == null ? "" : obj.get(field).getAsString());
    }

    public static <T> T parseObject(String o, Class<T> cls) {
        try {
            return JSONObject.parseObject(o, cls);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<T> parseArray(String o, Class<T> cls) {
        try {
            return JSONObject.parseArray(o, cls);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
