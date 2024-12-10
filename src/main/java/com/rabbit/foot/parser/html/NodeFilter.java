package com.rabbit.foot.parser.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.utils.BeanUtils;
import com.rabbit.foot.interceptors.HandlerInterceptors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

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
        if (jsonNode == null || !jsonNode.has(Constants.INTERCEPTORS)) {
            return handler;
        }

        JsonNode interceptorsNode = jsonNode.get(Constants.INTERCEPTORS);

        @SuppressWarnings("unchecked")
        HandlerInterceptors<String> interceptors = (HandlerInterceptors<String>)
                BeanUtils.getBean(interceptorsNode);
        String prefix = interceptorsNode.get(Constants.PREFIX).asText();
        String suffix = interceptorsNode.get(Constants.SUFFIX).asText();

        return interceptors.handle(handler, prefix, suffix);
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
        Iterator<Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            Element element = iterator.next();
            for (JsonNode action : removeArrayNode) {
                String elementType = action.get(Constants.REMOVE_ELEMENT_TYPE).asText();
                String elementValue = action.get(Constants.REMOVE_ELEMENT_VALUE).asText();
                switch (elementType) {
                    case NodeConstants.ID:
                        Element elementById = element.getElementById(elementValue);
                        if (elementById != null) {
                            iterator.remove();
                            continue;
                        }
                        break;
                    case NodeConstants.CLASS:
                        try {
                            Elements elementsByClass = element.getElementsByClass(elementValue);
                            if (!elementsByClass.isEmpty()) {
                                iterator.remove();
                                continue;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                        iterator.remove();
                        break;

                    case NodeConstants.TAGE:
                        try {
                            Elements elementsByTag = element.getElementsByTag(elementValue);
                            if (!elementsByTag.isEmpty()) {
                                iterator.remove();
                                continue;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                        iterator.remove();
                        break;
                    default:
                        try {
                            String attr = element.attr(elementType);
                            if (attr.equals(elementValue)) {
                                iterator.remove();
                                continue;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                        break;
                }
            }
        }
    }
}
