package com.liao.core.spider;

import com.fasterxml.jackson.databind.JsonNode;
import com.liao.core.ActionResources;
import com.liao.interceptors.HandlerInterceptors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 爬虫解析抽象类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-20
 */
@Slf4j
public abstract class SpiderResolver extends ActionResources {

    /**
     * 爬取的Web节点
     */
    protected Document webDocument;

    /**
     * 拦截器缓存
     */
    private static final Map<String, HandlerInterceptors<String>> interceptorsCache = new HashMap<>();

    /**
     * 前缀
     */
    private static final String PREFIX = "prefix";

    /**
     * 后缀
     */
    private static final String SUFFIX = "suffix";


    /**
     * 执行爬虫动作指定的拦截器
     *
     * @param handler  待处理的爬虫结果
     * @param jsonNode 拦截器描述
     */
    public String interceptors(String handler, JsonNode jsonNode) {
        if (jsonNode == null || !jsonNode.has("interceptors")) {
            return handler;
        }

        JsonNode interceptorsNode = jsonNode.get("interceptors");

        HandlerInterceptors<String> interceptors = getInterceptors(interceptorsNode);

        String prefix = interceptorsNode.get(PREFIX).asText();
        String suffix = interceptorsNode.get(SUFFIX).asText();

        return interceptors.handle(handler, prefix, suffix);
    }

    /**
     * 创建并获取拦截器
     *
     * @param jsonNode 拦截器描述
     */
    public HandlerInterceptors<String> getInterceptors(JsonNode jsonNode) {

        String javaType = jsonNode.get("type").asText();

        if (interceptorsCache.containsKey(javaType)) {
            return interceptorsCache.get(javaType);
        }

        try {
            HandlerInterceptors<String> interceptors = (HandlerInterceptors<String>) Class.forName(javaType).newInstance();
            interceptorsCache.put(javaType, interceptors);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException("名为 " + javaType + " 拦截器创建失败");
        }

        if (!interceptorsCache.containsKey(javaType)) {
            throw new RuntimeException("名为 " + javaType + " 拦截器创建失败");
        }

        return interceptorsCache.get(javaType);
    }
}
