package com.rabbit.foot.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 节点删除处理过滤器
 * 根据规则删除匹配到的节点
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class NodeRemoveFilter {

    /**
     * 节点过滤，删除非结果内容节点，如 Table 标题，防止结果结合出现属性为空的对象
     *
     * @param jsonNode 过滤规则
     * @param elements 节点集合
     */
    public static void filter(JsonNode jsonNode, Elements elements) {
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
