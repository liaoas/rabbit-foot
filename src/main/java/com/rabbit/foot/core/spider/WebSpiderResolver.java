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
                // 获取结果动作集合
                ArrayNode arrayNode = temp.action.withArray("result-element");

                // 存储单个结果对象
                ObjectNode objectNode = new ObjectMapper().createObjectNode();

                // 遍历结果集合，组装到一个结果对象里
                for (JsonNode jsonNode : arrayNode) {
                    SpiderTemp AResult = new SpiderTemp(objectNode, jsonNode, null, temp.obj, "obj");
                    packageAssembly(AResult);
                }

                temp.content.add(objectNode);
                break;
            default:
                if (temp.action.has("leaf-index")) {
                    int anInt = temp.action.get("leaf-index").asInt();
                    temp.obj = temp.obj.getElementsByAttribute(elementValue).get(anInt);
                    temp.lastType = "obj";
                } else {
                    temp.arr = temp.obj.getElementsByAttribute(elementValue);
                    temp.lastType = "arr";
                    nodeFiltering(temp.action, temp.arr);
                }
        }

    }

    /**
     * 递归解析结果节点描述部分并组装成结果对象
     */
    private void packageAssembly(SpiderTemp aResult) {
        if (ObjUtil.isNull(aResult.action)) {
            return;
        }

        if ("obj".equals(aResult.lastType)) {
            structuralAnalysisMap(aResult);
        } else {
            for (Element element : aResult.arr) {
                aResult.obj = element;
                structuralAnalysisMap(aResult);
            }
        }

        if (!aResult.action.has("element")) {
            return;
        }

        aResult.action = aResult.action.path("element");

        packageAssembly(aResult);
    }


    /**
     * 结果解析并填充 JsonObject 对象属性：属性值
     */
    private void structuralAnalysisMap(SpiderTemp aResult) {

        String elementType = aResult.action.get("element-type").asText();
        String elementValue = aResult.action.get("element-value").asText();

        switch (elementType) {
            case "id":
                aResult.obj = aResult.obj.getElementById(elementValue);
                results2Json(aResult.contentTemp, aResult.action, aResult.obj);
                aResult.lastType = "obj";
                break;
            case "class":
                if (aResult.action.has("leaf-index")) {
                    int anInt = aResult.action.get("leaf-index").asInt();
                    try {
                        aResult.obj = aResult.obj.getElementsByClass(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }
                    results2Json(aResult.contentTemp, aResult.action, aResult.obj);
                    aResult.lastType = "obj";

                } else {
                    aResult.arr = aResult.obj.getElementsByClass(elementValue);
                    nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = "arr";
                }
                break;

            case "tage":
                if (aResult.action.has("leaf-index")) {
                    int anInt = aResult.action.get("leaf-index").asInt();

                    try {
                        aResult.obj = aResult.obj.getElementsByTag(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(aResult.contentTemp, aResult.action, aResult.obj);

                    aResult.lastType = "obj";

                } else {
                    aResult.arr = aResult.obj.getElementsByTag(elementValue);
                    nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = "arr";
                }
                break;
            case "content":
                results2Json(aResult.contentTemp, aResult.action, aResult.obj);
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
        if (!action.has("is-leaf")) {
            return;
        }

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

    static class SpiderTemp {

        // 用于存储爬虫结果内容集合
        ArrayNode content;
        // 存储单条爬虫结果
        ObjectNode contentTemp;
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

        public SpiderTemp(ObjectNode contentTemp, JsonNode action, Elements arr, Element obj, String lastType) {
            this.contentTemp = contentTemp;
            this.action = action;
            this.arr = arr;
            this.obj = obj;
            this.lastType = lastType;
        }
    }

}
