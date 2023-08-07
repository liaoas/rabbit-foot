package com.liao.network;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.http.HttpRequest;
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
        String method = this.action.get("method").getAsString();
        if (method.equalsIgnoreCase(GET)) {
            return get();
        } else if (method.equals(POST)) {
            return null;
        }

        return null;
    }

    public T get() {
        HttpRequest httpRequest = null;
        if (action.has("url")) {
            httpRequest = HttpRequest.get(action.get("url").getAsString());
        }

        if (ObjUtil.isNull(httpRequest)) {
            throw new IllegalArgumentException("爬虫动作描述资源缺失");
        }

        if (action.has("params")) {
            Gson gson = new Gson();
            // 将 JsonObject 转换为 Map
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> map = gson.fromJson(action.get("params"), type);

            httpRequest = httpRequest.form(map);
        }

        String body = httpRequest.execute().body();

        return (T) body;
    }
}
