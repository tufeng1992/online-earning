package com.powerboot.common;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpRequestUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

    public static JSONObject doGet(String requestUrl, Map<String, Object> requestParam) {
        return doGet(requestUrl, requestParam, false, null);
    }

    public static JSONObject doGet(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                   Map<String, String> headValue) {
        try {
            List<String> keys = new ArrayList<>(requestParam.keySet());
            logger.info("requestURL : {}", requestUrl);
            logger.info("------------requestParam : {} ", requestParam);
            logger.info("------------requestHead : {} ", headValue);
            //发送get请求-----start
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();
            //设置请求头
            if (isHead) {
                List<String> headKeys = new ArrayList<>(headValue.keySet());
                for (String headKey : headKeys) {
                    request = request.newBuilder().addHeader(headKey, headValue.get(headKey)).build();
                }
            }
            HttpUrl.Builder httpBuilder = HttpUrl.parse(requestUrl).newBuilder();
            if (requestParam != null) {
                for (String param : keys) {
                    httpBuilder.addQueryParameter(param, requestParam.get(param).toString());
                }
            }
            request = request.newBuilder().url(httpBuilder.build()).build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            //发送get请求-----end
            logger.info("requestURL : {} ---------- response : {}", requestUrl, res);
            if (StringUtils.isBlank(res)) {
                return new JSONObject();
            }
            return new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray doPostJsonArray(String requestUrl, Map<String, Object> requestParam) {
        return doPostArray(requestUrl, requestParam, false, null,PostContentEnum.JSON);
    }

    public static JSONObject doPostJson(String requestUrl, Map<String, Object> requestParam) {
        return doPostJson(requestUrl, requestParam, false, null);
    }

    public static JSONObject doPostJson(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                        Map<String, String> headValue) {
        return doPost(requestUrl, requestParam, isHead, headValue, PostContentEnum.JSON);
    }


    public static JSONObject doPostForm(String requestUrl, Map<String, Object> requestParam) {
        return doPostForm(requestUrl, requestParam, false, null);
    }

    public static JSONObject doPostForm(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                        Map<String, String> headValue) {
        return doPost(requestUrl, requestParam, isHead, headValue, PostContentEnum.FORM);
    }

    public static JSONObject doPostFormWWW(String requestUrl, Map<String, Object> requestParam) {
        return doPostFormWWW(requestUrl, requestParam, false, null);
    }

    public static JSONObject doPostFormWWW(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                           Map<String, String> headValue) {
        return doPost(requestUrl, requestParam, isHead, headValue, PostContentEnum.FORM_WWW);
    }

    private static JSONObject doPost(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                     Map<String, String> headValue, PostContentEnum postContentEnum) {
        try {
            String res = post(requestUrl, requestParam, isHead, headValue, postContentEnum);
            if (StringUtils.isBlank(res)) {
                return new JSONObject();
            }
            return new JSONObject(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static JSONArray doPostArray(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                                     Map<String, String> headValue, PostContentEnum postContentEnum) {
        try {
            String res = post(requestUrl, requestParam, isHead, headValue, postContentEnum);
            if (StringUtils.isBlank(res)) {
                return null;
            }
            return new JSONArray(res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String post(String requestUrl, Map<String, Object> requestParam, boolean isHead,
                               Map<String, String> headValue, PostContentEnum postContentEnum){
        try {
            logger.info("requestURL : {}", requestUrl);
            logger.info("------------requestParam : {} ", requestParam);
            //send post-----start
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = null;
            Response response = null;
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();
            //设置请求头
            if (isHead) {
                List<String> headKeys = new ArrayList<>(headValue.keySet());
                for (String headKey : headKeys) {
                    request = request.newBuilder().addHeader(headKey, headValue.get(headKey)).build();
                }
            }
            switch (postContentEnum) {
                case FORM_WWW:

                case FORM:
                    FormBody.Builder formBody = new FormBody.Builder();
                    List<String> formKeys = new ArrayList<>(requestParam.keySet());
                    for (String formKey : formKeys) {
                        formBody.add(formKey, requestParam.get(formKey).toString());
                    }
                    requestBody = formBody.build();
                    request = request.newBuilder().post(requestBody).build();
                    response = client.newCall(request).execute();
                    break;
                case JSON:
                    requestBody = RequestBody.create(PostContentEnum.JSON.getMediaType(), JSON.toJSON(requestParam).toString());
                    request = request.newBuilder().post(requestBody).build();
                    response = client.newCall(request).execute();
                    break;
            }
            logger.info("------------requestParamJson : {} ", JSON.toJSON(requestParam).toString());
            String res = response.body().string();
            logger.info("requestURL : {} ---------- response : {}", requestUrl, res);
            return res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
