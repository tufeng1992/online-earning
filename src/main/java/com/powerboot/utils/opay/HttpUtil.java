package com.powerboot.utils.opay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Verify;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

@Slf4j
public final class HttpUtil {
    public static final MediaType STR_JSON_UTF_8 = MediaType.parse(com.google.common.net.MediaType.JSON_UTF_8.toString());
    public static final MediaType MEDIA_TYPE_JSON_UTF_8 = STR_JSON_UTF_8;
    private static OkHttpClient HTTP_CLIENT;

    private HttpUtil() {
    }

    public static OkHttpClient getHttpClient() {
        if (HTTP_CLIENT == null) {
            synchronized (HttpUtil.class) {
                if (HTTP_CLIENT == null) {
                    HttpLoggingInterceptor loggingInterceptor = newLoggingInterceptor();
                    HTTP_CLIENT = new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(60, TimeUnit.SECONDS)
                            .addInterceptor(loggingInterceptor)
                            .build();
                }
            }
        }
        return HTTP_CLIENT;
    }

    public static OkHttpClient.Builder newHttpClientBuilder() {
        return getHttpClient().newBuilder();
    }

    public static Request newGetRequest(String url) {
        return newGetRequest(url, null, null);
    }

    public static Request newGetRequest(String url, Map<String, String> queryParams) {
        return newGetRequest(url, queryParams, null);
    }

    public static Request newGetRequest(String url, Map<String, String> queryParams, Map<String, String> headers) {
        return newRequest(url, queryParams, headers).get().build();
    }

    public static Request newPostRequest(String url, RequestBody body) {
        return newPostRequest(url, null, null, body);
    }

    public static Optional<JSONObject> invokePostRequest(String url, RequestBody body ) {
//        try (Response response = invoke(newPostRequest(url, body))) {
//            if (response.isSuccessful()) {
//                byte[] bytes = response.body().bytes();
//                if (bytes == null) {
//                    return Optional.empty();
//                }
//                return Optional.ofNullable(mapper.apply(bytes));
//            }
//            return Optional.empty();
//        }
        Request request = newPostRequest(url, body);
        return invoke4JsonObj(request);
    }

    public static Request newPostRequest(String url, Map<String, String> queryParams, RequestBody body) {
        return newPostRequest(url, queryParams, null, body);
    }

    public static Request newPostRequest(String url, Map<String, String> queryParams, Map<String, String> headers, RequestBody body) {
        return newRequest(url, queryParams, headers).post(body).build();
    }

    public static Request.Builder newRequest(String url, Map<String, String> queryParams, Map<String, String> headers) {
        Verify.verifyNotNull(url);
        url = url.trim();
        Request.Builder builder = new Request.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(builder::header);
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new IllegalArgumentException("url不合法");
        }
        if (!CollectionUtils.isEmpty(queryParams)) {
            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            queryParams.forEach(urlBuilder::addQueryParameter);
            httpUrl = urlBuilder.build();
        }
        return builder.url(httpUrl);
    }

    public static RequestBody newJsonRequestBody(Object object) {
        Verify.verifyNotNull(object);
        byte[] bytes = JSON.toJSONBytes(object);
        return RequestBody.create(MEDIA_TYPE_JSON_UTF_8, bytes);
    }

    public static MultipartBody.Builder newMultiPartRequestBody() {
        return new MultipartBody.Builder();
    }

    public static Optional<JSONObject> get4JsonObj(String url) {
        return get4JsonObj(url, null, null);
    }

    public static Optional<JSONObject> get4JsonObj(String url, Map<String, String> queryParams) {
        return get4JsonObj(url, queryParams, null);
    }

    public static Optional<JSONObject> get4JsonObj(String url, Map<String, String> queryParams, Map<String, String> headers) {
        return invoke4JsonObj(newGetRequest(url, queryParams, headers));
    }

    public static Optional<JSONObject> post4JsonObj(String url, Object body) {
        return post4JsonObj(url, null, body);
    }

    public static Optional<JSONObject> post4JsonObj(String url, Map<String, String> headers, Object body) {
        Request request = newPostRequest(url, null, headers, newJsonRequestBody(body));
        return invoke4JsonObj(request);
    }

    @SneakyThrows
    public static <T> Optional<T> invoke4JavaType(Request request, Class<T> type) {
        return invoke4Obj(request, body -> JSON.parseObject(StringUtils.newStringUtf8(body), type));
    }

    @SneakyThrows
    public static <T> Optional<T> invoke4JavaType(Request request, TypeReference<T> type) {
        return invoke4Obj(request, body -> JSON.parseObject(StringUtils.newStringUtf8(body), type));
    }

    @SneakyThrows
    public static Optional<JSONObject> invoke4JsonObj(Request request) {
        return invoke4Obj(request, body -> JSON.parseObject(StringUtils.newStringUtf8(body)));
    }

    @SneakyThrows
    public static Optional<JSONArray> invoke4JsonArr(Request request) {
        return invoke4Obj(request, body -> JSON.parseArray(StringUtils.newStringUtf8(body)));
    }

    @SneakyThrows
    public static <T> Optional<T> invoke4Obj(Request request, BodyMapper<T> mapper) {
        Verify.verifyNotNull(request);
        Verify.verifyNotNull(mapper);
        try (Response response = invoke(request)) {
            if (response.isSuccessful()) {
                byte[] bytes = response.body().bytes();
                if (bytes == null) {
                    return Optional.empty();
                }
                return Optional.ofNullable(mapper.apply(bytes));
            }
            return Optional.empty();
        }
    }

    @SneakyThrows
    public static <T> T invoke(Request request, ResponseMapper<T> mapper) {
        Verify.verifyNotNull(request);
        Verify.verifyNotNull(mapper);
        try (Response response = invoke(request)) {
            return mapper.apply(response);
        }
    }

    @SneakyThrows
    public static Response invoke(Request request) {
        Verify.verifyNotNull(request);
        return getHttpClient().newCall(request).execute();
    }

    private static HttpLoggingInterceptor newLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::info);
        loggingInterceptor.setLevel(BODY);
        return loggingInterceptor;
    }

    public static interface ResponseMapper<T> {
        T apply(Response response) throws IOException;
    }

    public static interface BodyMapper<T> {
        T apply(byte[] body) throws IOException;
    }
}