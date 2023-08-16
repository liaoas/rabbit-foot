package com.liao.network;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * <p>
 * 动态 Http 请求，根据配置 Json 进行请求指定的数据
 * </p>
 *
 * @author LiAo
 * @since 2023-08-07
 */
@Data
@SuppressWarnings("unchecked")
public class HttpAsk<T> {

    private T data;

    private static final String GET = "GET";

    private static final String POST = "POST";

    // 存储 Http 请求动作
    private JsonObject action;

    public HttpAsk(JsonObject action) {
        this.action = action;
    }

    /**
     * 执行请求
     *
     * @return 结果
     */
    public T execute() {

        HttpRequest httpRequest = httpRequestBuild();

        String method = this.action.get("method").getAsString();

        if (method.equalsIgnoreCase(GET)) {
            return get(httpRequest);
        } else if (method.equals(POST)) {
            return post(httpRequest);
        }

        return null;
    }

    /**
     * 构建请求体
     *
     * @return 请求对象
     */
    private HttpRequest httpRequestBuild() {
        HttpRequest httpRequest = null;

        if (action.has("url")) {
            httpRequest = HttpRequest.of(action.get("url").getAsString());
        }

        if (ObjUtil.isNull(httpRequest)) {
            throw new IllegalArgumentException("爬虫动作描述资源缺失");
        }

        if (action.has("headers")) {
            Gson gson = new Gson();
            // 将 JsonObject 转换为 Map
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> headers = gson.fromJson(action.get("headers"), type);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                httpRequest = httpRequest.header(key, value);
            }
        }

        if (action.has("params")) {
            Gson gson = new Gson();
            // 将 JsonObject 转换为 Map
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> form = gson.fromJson(action.get("params"), type);

            httpRequest = httpRequest.form(form);
        }

        if (action.has("body")) {
            JsonObject body = action.get("body").getAsJsonObject();
            httpRequest = httpRequest.body(String.valueOf(body));
        }

        return httpRequest;
    }

    /**
     * Get 请求
     *
    * @return 目标地址
     */
    private T get(HttpRequest httpRequest) {

        String body = httpRequest.method(Method.GET).execute().body();

        return (T) body;
    }


    /**
     * Get 请求
     *
     * @return 目标地址
     */
    private T post(HttpRequest httpRequest) {

        String body = httpRequest.method(Method.POST).execute().body();

        return (T) body;
    }
}
