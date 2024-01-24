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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

            if (Clazz.getName().equals(String.class.getName())) {
                List<String> value = IntStream.range(0, arrayNode.size()).mapToObj(index -> arrayNode.get(index).get("value").asText()).collect(Collectors.toList());
                @SuppressWarnings("unchecked") List<T> newList = (List<T>) value;
                return newList;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readerForListOf(Clazz).readValue(arrayNode.toString());
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

        SpiderTemp temp = new SpiderTemp(arrayNode, node, null, this.webDocument, "obj");

        resolver(temp);

        return arrayNode;
    }

    /**
     * 递归解析HTML节点至结果节点->packageAssembly()
     *
     * @param temp 存储爬虫解析参数
     */
    private void resolver(SpiderTemp temp) {
        if (ObjUtil.isNull(temp.action)) {
            return;
        }

        if ("obj".equals(temp.lastType)) {
            getElement(temp);
        } else {
            for (Element element : temp.arr) {
                temp.obj = element;
                getElement(temp);
            }
        }

        if (!temp.action.has("element")) {
            return;
        }

        // 下一次动作
        temp.action = temp.action.path("element");

        // 递归下一次
        resolver(temp);
    }


    /**
     * 根绝爬虫描述，获取节点
     *
     * @param temp 存储爬虫解析参数
     */
    private void getElement(SpiderTemp temp) {

        String elementType = temp.action.get("element-type").asText();

        String elementValue = temp.action.get("element-value").asText();

        switch (elementType) {
            case "id":
                temp.obj = temp.obj.getElementById(elementValue);
                temp.lastType = "obj";
                break;
            case "class":
                if (temp.action.has("leaf-index")) {
                    int anInt = temp.action.get("leaf-index").asInt();
                    temp.obj = temp.obj.getElementsByClass(elementValue).get(anInt);
                    temp.lastType = "obj";
                } else {
                    temp.arr = temp.obj.getElementsByClass(elementValue);
                    temp.lastType = "arr";
                    nodeFiltering(temp.action, temp.arr);
                }
                break;

            case "tage":
                if (temp.action.has("leaf-index")) {
                    int anInt = temp.action.get("leaf-index").asInt();
                    temp.obj = temp.obj.getElementsByTag(elementValue).get(anInt);
                    temp.lastType = "obj";
                } else {
                    temp.arr = temp.obj.getElementsByTag(elementValue);
                    temp.lastType = "arr";
                    nodeFiltering(temp.action, temp.arr);
                }

                break;
            case "result":
                // 判断结果伪动作，标记开始组装结果
                if (!temp.action.has("result-element")) {
                    break;
                }

                ArrayNode arrayNode = temp.action.withArray("result-element");

                ObjectNode objectNode = new ObjectMapper().createObjectNode();

                for (JsonNode jsonNode : arrayNode) {
                    packageAssembly(objectNode, jsonNode, null, temp.obj, "obj");
                }

                temp.content.add(objectNode);
                break;
        }

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
            structuralAnalysisMap(contentTemp, action, elementType, elementValue, obj);
        } else {
            for (Element element : arr) {
                structuralAnalysisMap(contentTemp, action, elementType, elementValue, element);
            }
        }

        if (!action.has("element")) {
            return;
        }

        action = action.path("element");

        packageAssembly(contentTemp, action, arr, obj, lastType);
    }


    /**
     * 结果解析并填充 JsonObject 对象属性：属性值
     *
     * @param contentTemp  存储结果
     * @param action       爬虫动作
     * @param elementType  节点类型
     * @param elementValue 节点值
     * @param element      待解析的节点
     */
    private void structuralAnalysisMap(ObjectNode contentTemp, JsonNode action, String elementType, String elementValue,
                                       Element element) {
        Elements arr;
        switch (elementType) {
            case "id":
                element = element.getElementById(elementValue);
                results2Json(contentTemp, action, element);

                break;
            case "class":
                if (action.has("leaf-index")) {
                    int anInt = action.get("leaf-index").asInt();
                    try {
                        element = element.getElementsByClass(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(contentTemp, action, element);

                } else {
                    arr = element.getElementsByClass(elementValue);
                    nodeFiltering(action, arr);
                }
                break;

            case "tage":
                if (action.has("leaf-index")) {
                    int anInt = action.get("leaf-index").asInt();

                    try {
                        element = element.getElementsByTag(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(contentTemp, action, element);

                } else {
                    arr = element.getElementsByTag(elementValue);
                    nodeFiltering(action, arr);
                }
                break;
            case "content":
                results2Json(contentTemp, action, element);
                break;
        }
    }

    /**
     * 将结果组装为Json对象
     *
     * @param contentTemp 结果对象
     * @param action      爬虫动作描述
     * @param obj         要解析的结果
     */
    private void results2Json(ObjectNode contentTemp, JsonNode action, Element obj) {
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
    }

    static class SpiderTemp {

        // 用于存储爬虫的结果内容
        ArrayNode content;
        // 爬虫动作
        JsonNode action;
        // 存储多个节点
        Elements arr;
        // 存储单个节点 | 要解析的节点
        Element obj;
        // 上一次节点类型；
        String lastType;

        public SpiderTemp(ArrayNode content, JsonNode action, Elements arr, Element obj, String lastType) {
            this.content = content;
            this.action = action;
            this.arr = arr;
            this.obj = obj;
            this.lastType = lastType;
        }
    }

}
