package com.rabbit.foot.network;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.utils.ConvertUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * <p>
 * 动态 Http 请求，根据配置 Json 进行请求指定的数据
 * </p>
 *
 * @author LiAo
 * @since 2023-08-07
 */
public class HttpAsk<T> {

    private static final String GET = "GET";

    private static final String POST = "POST";

    // 存储 Http 请求动作
    private final JsonNode action;

    public HttpAsk(JsonNode action) {
        this.action = action;
    }

    /**
     * 执行请求
     *
     * @return 结果
     */
    public T execute() {
        HttpRequest httpRequest = buildHttpRequest();
        String method = action.get(Constants.METHOD).asText();

        if (method.equalsIgnoreCase(GET)) {
            return sendRequest(httpRequest);
        } else if (method.equalsIgnoreCase(POST)) {
            return sendRequest(httpRequest);
        } else {
            throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }
    }

    /**
     * 构建请求
     *
     * @return HttpRequest
     */
    private HttpRequest buildHttpRequest() {
        if (!action.has(Constants.URL)) {
            throw new IllegalArgumentException("URL is missing in the action");
        }

        String url = action.get(Constants.URL).asText();
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));

        // 添加 Headers
        if (action.has(Constants.HEADERS)) {
            Map<String, String> headers = ConvertUtils.jsonNodeToMapStrStr(action.get(Constants.HEADERS));
            headers.forEach(builder::header);
        }

        // 添加请求方法及 Body
        String method = action.get(Constants.METHOD).asText();
        if (method.equalsIgnoreCase(POST)) {
            if (action.has(Constants.HEADERS)) {
                String body = action.get(Constants.HEADERS).asText();
                builder.POST(HttpRequest.BodyPublishers.ofString(body));
            } else {
                builder.POST(HttpRequest.BodyPublishers.noBody());
            }
        } else {
            builder.GET();
        }

        return builder.build();
    }

    /**
     * 发送请求
     *
     * @param httpRequest 请求对象
     * @return 响应结果
     */
    private T sendRequest(HttpRequest httpRequest) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response.body());
        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed", e);
        }
    }

    /**
     * 解析响应
     *
     * @param responseBody 响应体
     * @return 泛型结果
     */
    @SuppressWarnings("unchecked")
    private T parseResponse(String responseBody) {
        // 假设直接返回响应体字符串
        return (T) responseBody;
    }
}
