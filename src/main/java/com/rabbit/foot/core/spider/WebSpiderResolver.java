package com.rabbit.foot.core.spider;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.network.HttpAsk;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * HTML 爬虫解析器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-16
 */
public class WebSpiderResolver<T> extends SpiderResolver implements Resolver<T> {

    /**
     * 爬虫执行
     */
    @Override
    public List<T> execute(JsonNode activeRes) {
        // 获取动作
        super.activeRes = activeRes;

        if (ObjUtil.isNull(activeRes)) {
            return null;
        }

        // 爬取目标网站内容
        requestWebPage(activeRes);

        if (ObjUtil.isNull(this.webDocument)) {
            return null;
        }

        // 解析 HTML 节点
        ArrayNode arrayNode = webElementResolver(activeRes);

        if (arrayNode != null && arrayNode.size() <= 0) {
            throw new RuntimeException("抓取目标网站失败");
        }

        assert arrayNode != null;

        return convert(activeRes, arrayNode);
    }

    /**
     * 将结果转换为实体类对象
     *
     * @param action    动作
     * @param arrayNode 结果集合
     * @return T
     */
    private List<T> convert(JsonNode action, ArrayNode arrayNode) {
        JsonNode configObj = action.path("config");

        String javaType = configObj.get("java-type").asText();
        try {
            Class<?> Clazz = Class.forName(javaType);

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper
                    .readerForListOf(Clazz)
                    .readValue(arrayNode.toString());
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据指定的爬虫动作抓取目标内容页面
     *
     * @param httpAction http 执行动作
     */
    private void requestWebPage(JsonNode httpAction) {

        JsonNode siteObj = httpAction.path("site");

        HttpAsk<String> httpAsk = new HttpAsk<>(siteObj);

        String content = httpAsk.execute();

        if (StrUtil.isEmpty(content)) {
            return;
        }
        this.webDocument = Jsoup.parse(content);
    }

    /**
     * 根据爬虫动作解析HTML
     *
     * @param resolverAction 解析动作
     * @return 爬取结果
     */
    private ArrayNode webElementResolver(JsonNode resolverAction) {
        if (ObjUtil.isNull(resolverAction)) {
            return null;
        }

        JsonNode node = resolverAction.path("resolver-action");
        node = node.path("element");
        JsonMapper resultMap = new JsonMapper();

        ArrayNode arrayNode = resultMap.createArrayNode();

        resolver(arrayNode, node, null, this.webDocument, "obj");

        return arrayNode;
    }

    /**
     * 递归解析HTML节点
     *
     * @param content  用于存储爬虫的结果内容
     * @param action   动作描述
     * @param arr      爬虫进度指针 多节点
     * @param obj      爬虫进度指针 单节点
     * @param lastType 上次节点类型
     */
    private void resolver(ArrayNode content, JsonNode action, Elements arr, Element obj, String lastType) {
        if (ObjUtil.isNull(action)) {
            return;
        }

        String elementType = action.get("element-type").asText();

        String elementValue = action.get("element-value").asText();

        if ("obj".equals(lastType)) {
            switch (elementType) {
                case "id":
                    obj = obj.getElementById(elementValue);
                    lastType = "obj";
                    break;
                case "class":
                    arr = obj.getElementsByClass(elementValue);
                    lastType = "arr";
                    break;
            }
        } else {
            for (Element element : arr) {
                switch (elementType) {
                    case "id":
                        if (action.has("leaf-index")) {
                            obj = element.getElementById(elementValue);
                            lastType = "obj";
                        } else {
                            obj = element.getElementById(elementValue);
                            lastType = "arr";
                        }
                        break;
                    case "class":
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByClass(elementValue).get(anInt);
                            lastType = "obj";
                        } else {
                            arr = element.getElementsByClass(elementValue);
                            lastType = "arr";
                        }
                        break;

                    case "tage":
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByTag(elementValue).get(anInt);
                            lastType = "obj";
                        } else {
                            arr = element.getElementsByTag(elementValue);
                            lastType = "arr";
                        }

                        break;
                    case "result":
                        // 判断结果伪动作，标记开始组装结果
                        if (!action.has("result-element")) {
                            break;
                        }

                        ArrayNode arrayNode = action.withArray("result-element");

                        ObjectNode objectNode = new ObjectMapper().createObjectNode();

                        for (JsonNode jsonNode : arrayNode) {
                            packageAssembly(objectNode, jsonNode, null, element, "obj");
                        }

                        content.add(objectNode);
                        break;
                }

            }
        }

        if (!action.has("element")) {
            return;
        }

        // 下一次动作
        action = action.path("element");

        // 递归下一次
        resolver(content, action, arr, obj, lastType);
    }

    /**
     * 递归解析结果节点描述部分并组装成结果对象
     *
     * @param contentTemp 单个结果对象
     * @param action      动作
     * @param arr         存储本次解析集合节点对象，用作下一次递归
     * @param obj         存储本次解析节点对象，用作下一次递归
     * @param lastType    标记一下次解析结果类型
     */
    private void packageAssembly(ObjectNode contentTemp, JsonNode action, Elements arr, Element obj, String lastType) {
        if (ObjUtil.isNull(action)) {
            return;
        }
        String elementType = action.get("element-type").asText();
        String elementValue = action.get("element-value").asText();

        if ("obj".equals(lastType)) {
            structuralAnalysisMap(contentTemp, action, elementType, elementValue, obj, arr, obj, lastType);
        } else {
            for (Element element : arr) {
                structuralAnalysisMap(contentTemp, action, elementType, elementValue, element, arr, obj, lastType);
            }
        }

        if (!action.has("element")) {
            return;
        }

        action = action.path("element");

        packageAssembly(contentTemp, action, arr, obj, lastType);
    }


    /**
     * 结果解析并映射
     *
     * @param contentTemp  存储结果
     * @param action       爬虫动作
     * @param elementType  节点类型
     * @param elementValue 节点值
     * @param element      待解析的节点
     * @param arr          存储本次解析返回的集合节点对象，用作下一次递归
     * @param obj          存储本次解析返回的节点对象，用作下一次递归
     * @param lastType     下一次解析类型
     */
    private void structuralAnalysisMap(ObjectNode contentTemp, JsonNode action, String elementType, String elementValue,
                                       Element element, Elements arr, Element obj, String lastType) {
        switch (elementType) {
            case "id":
                if (action.has("leaf-index")) {
                    obj = element.getElementById(elementValue);

                    results2Json(contentTemp, action, obj, lastType);

                } else {
                    obj = element.getElementById(elementValue);
                    lastType = "obj";
                }
                break;

            case "class":
                if (action.has("leaf-index")) {
                    int anInt = action.get("leaf-index").asInt();
                    try {
                        obj = element.getElementsByClass(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(contentTemp, action, obj, lastType);

                } else {
                    arr = element.getElementsByClass(elementValue);
                    lastType = "arr";
                }
                break;

            case "tage":
                if (action.has("leaf-index")) {
                    int anInt = action.get("leaf-index").asInt();

                    try {
                        obj = element.getElementsByTag(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(contentTemp, action, obj, lastType);

                } else {
                    arr = element.getElementsByTag(elementValue);
                    lastType = "arr";
                }
                break;
        }
    }

    /**
     * 将结果组装为Json对象
     *
     * @param contentTemp 结果对象
     * @param action      爬虫动作描述
     * @param obj         要解析的结果
     * @param lastType    下一次解析类型
     */
    private void results2Json(ObjectNode contentTemp, JsonNode action, Element obj, String lastType) {
        if (action.has("is-leaf")) {

            if (!action.has("target-key")) {
                return;
            }

            String target = action.get("target-key").asText();

            if (target.equals("text")) {
                String text = obj.text();
                contentTemp.put(action.get("result-key").asText(), interceptors(text, action));
            } else {
                String attribute = obj.attr(target);
                contentTemp.put(action.get("result-key").asText(), interceptors(attribute, action));
            }
        }
        lastType = "obj";
    }

}
