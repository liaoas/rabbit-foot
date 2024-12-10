package com.rabbit.foot.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.interceptors.HandlerInterceptors;
import com.rabbit.foot.utils.BeanUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    public static String interceptors(String handler, JsonNode jsonNode) {
        // 如果 jsonNode 为 null 或没有 interceptors 字段，直接返回 handler
        if (jsonNode == null || !jsonNode.has(Constants.INTERCEPTORS)) {
            return handler;
        }

        JsonNode interceptorsNode = jsonNode.get(Constants.INTERCEPTORS);

        // 获取 HandlerInterceptors 类型的 Bean
        HandlerInterceptors<String> interceptors = getInterceptors(interceptorsNode);

        // 获取前缀和后缀，提供默认值以防缺失
        String prefix = interceptorsNode.has(Constants.PREFIX) ? interceptorsNode.get(Constants.PREFIX).asText() : "";
        String suffix = interceptorsNode.has(Constants.SUFFIX) ? interceptorsNode.get(Constants.SUFFIX).asText() : "";

        // 处理并返回最终结果
        return interceptors.handle(handler, prefix, suffix);
    }

    /**
     * 获取 HandlerInterceptors 实例，确保类型安全。
     */
    private static HandlerInterceptors<String> getInterceptors(JsonNode interceptorsNode) {
        try {
            // 使用 BeanUtils 获取 Bean，同时保证类型安全
            return (HandlerInterceptors<String>) BeanUtils.getBean(interceptorsNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get HandlerInterceptors bean", e);
        }
    }


    /**
     * 节点过滤，删除非结果内容节点，如 Table 标题，防止结果结合出现属性为空的对象
     *
     * @param jsonNode 过滤规则
     * @param elements 节点集合
     */
    public static void nodeFiltering(JsonNode jsonNode, Elements elements) {
        if (jsonNode == null || !jsonNode.has(Constants.REMOVE) || elements == null || elements.isEmpty()) {
            return;
        }

        ArrayNode removeArrayNode = jsonNode.withArray(Constants.REMOVE);

        elements.removeIf(element -> shouldRemoveElement(element, removeArrayNode));
    }

    private static boolean shouldRemoveElement(Element element, ArrayNode removeArrayNode) {
        for (JsonNode action : removeArrayNode) {
            String elementType = action.get(Constants.REMOVE_ELEMENT_TYPE).asText();
            String elementValue = action.get(Constants.REMOVE_ELEMENT_VALUE).asText();

            if (matchesCondition(element, elementType, elementValue)) {
                return true; // Remove the element if any condition matches
            }
        }
        return false;
    }

    private static boolean matchesCondition(Element element, String elementType, String elementValue) {
        return switch (elementType) {
            case NodeConstants.ID -> matchesById(element, elementValue);
            case NodeConstants.CLASS -> matchesByClass(element, elementValue);
            case NodeConstants.TAGE -> matchesByTag(element, elementValue);
            default -> matchesByAttribute(element, elementType, elementValue);
        };
    }

    private static boolean matchesById(Element element, String elementValue) {
        Element elementById = element.getElementById(elementValue);
        return elementById != null;
    }

    private static boolean matchesByClass(Element element, String elementValue) {
        try {
            Elements elementsByClass = element.getElementsByClass(elementValue);
            return !elementsByClass.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesByTag(Element element, String elementValue) {
        try {
            Elements elementsByTag = element.getElementsByTag(elementValue);
            return !elementsByTag.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesByAttribute(Element element, String elementType, String elementValue) {
        try {
            String attr = element.attr(elementType);
            return attr.equals(elementValue);
        } catch (Exception e) {
            return false;
        }
    }

}
