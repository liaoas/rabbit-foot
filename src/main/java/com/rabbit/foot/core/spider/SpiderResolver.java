package com.rabbit.foot.core.spider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.core.ActionResources;
import com.rabbit.foot.interceptors.HandlerInterceptors;
import com.rabbit.foot.utils.BeanUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * 爬虫解析抽象类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-20
 */
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

        HandlerInterceptors<String> interceptors = (HandlerInterceptors<String>) BeanUtils.getBean(interceptorsNode);

        String prefix = interceptorsNode.get(PREFIX).asText();
        String suffix = interceptorsNode.get(SUFFIX).asText();

        return interceptors.handle(handler, prefix, suffix);
    }

    /**
     * 节点过滤，删除非结果内容节点，如 Table 标题，防止结果结合出现属性为空的对象
     *
     * @param jsonNode 过滤规则
     * @param elements 节点集合
     */
    public void nodeFiltering(JsonNode jsonNode, Elements elements) {
        if (jsonNode == null || !jsonNode.has("remove") || elements == null || elements.isEmpty()) {
            return;
        }

        ArrayNode removeArrayNode = jsonNode.withArray("remove");

        Iterator<Element> iterator = elements.iterator();

        while (iterator.hasNext()) {
            Element element = iterator.next();

            for (JsonNode action : removeArrayNode) {

                String elementType = action.get("element-type").asText();

                String elementValue = action.get("element-value").asText();
                switch (elementType) {
                    case "id":
                        Element elementById = element.getElementById(elementValue);

                        if (elementById != null) {
                            iterator.remove();
                            continue;
                        }
                        break;
                    case "class":
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

                    case "tage":
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
