package com.rabbit.foot.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.interceptors.Interceptors;
import com.rabbit.foot.utils.BeanUtils;

/**
 * 节点过滤器
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class NodeFilter {


    /**
     * 执行爬虫行为指定的拦截器
     *
     * @param handler  待处理的爬虫结果
     * @param jsonNode 拦截器描述
     */
    public static String filter(String handler, JsonNode jsonNode) {
        // 如果 jsonNode 为 null 或没有 interceptors 字段，直接返回 handler
        if (jsonNode == null || !jsonNode.has(Constants.INTERCEPTORS)) {
            return handler;
        }

        JsonNode interceptorsNode = jsonNode.get(Constants.INTERCEPTORS);

        // 获取 HandlerInterceptors 类型的 Bean
        Interceptors<String> interceptors = getInterceptors(interceptorsNode);

        // 获取前缀和后缀，提供默认值以防缺失
        String prefix = interceptorsNode.has(Constants.PREFIX) ? interceptorsNode.get(Constants.PREFIX).asText() : "";
        String suffix = interceptorsNode.has(Constants.SUFFIX) ? interceptorsNode.get(Constants.SUFFIX).asText() : "";

        // 处理并返回最终结果
        return interceptors.handle(handler, prefix, suffix);
    }

    /**
     * 获取 HandlerInterceptors 实例，确保类型安全。
     */
    @SuppressWarnings("unchecked")
    private static Interceptors<String> getInterceptors(JsonNode interceptorsNode) {
        try {
            Object bean = BeanUtils.getBean(interceptorsNode);
            if (bean instanceof Interceptors<?>) {
                return (Interceptors<String>) bean;
            } else {
                throw new IllegalArgumentException("Bean is not of expected type");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get HandlerInterceptors bean", e);
        }
    }





}
