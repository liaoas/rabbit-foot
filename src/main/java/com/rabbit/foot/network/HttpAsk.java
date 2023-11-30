package com.rabbit.foot.network;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.fasterxml.jackson.databind.JsonNode;
import com.rabbit.foot.utils.ConvertUtils;
import lombok.Data;

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
    private JsonNode action;

    public HttpAsk(JsonNode action) {
        this.action = action;
    }

    /**
     * 执行请求
     *
     * @return 结果
     */
    public T execute() {

        HttpRequest httpRequest = httpRequestBuild();

        String method = this.action.get("method").asText();

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
            httpRequest = HttpRequest.of(action.get("url").asText());
        }

        if (ObjUtil.isNull(httpRequest)) {
            throw new IllegalArgumentException("爬虫动作描述资源缺失");
        }

        if (action.has("headers")) {
            // 将 JsonObject 转换为 Map
            Map<String, String> result = ConvertUtils.jsonNodeToMapStrStr(action.get("headers"));
            for (Map.Entry<String, String> header : result.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                httpRequest = httpRequest.header(key, value);
            }
        }

        if (action.has("params")) {
            // 将 JsonObject 转换为 Map
            Map<String, Object> result = ConvertUtils.jsonNodeToMapStrObj(action.get("params"));

            httpRequest = httpRequest.form(result);
        }

        if (action.has("body")) {
            httpRequest = httpRequest.body(action.get("body").asText());
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
