package com.rabbit.foot.parser.html;

import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.constant.NodeConstants;
import org.jsoup.nodes.Element;

/**
 * 解析内容节点，并且将内容组装成指定的类型
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class ElementResultParser {

    public static void parser(HtmlNodeTemp temp) {
        // 判断结果伪动作，标记开始组装结果
        if (!temp.action.has(Constants.RESULT_ELEMENT)) {
            return;
        }
        // 获取结果动作集合
        ArrayNode arrayNode = temp.action.withArray(Constants.RESULT_ELEMENT);

        // 存储单个结果对象
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        // 遍历结果集合，组装到一个结果对象里
        for (JsonNode jsonNode : arrayNode) {
            HtmlNodeTemp AResult = new HtmlNodeTemp(objectNode, jsonNode, null, temp.obj, NodeConstants.OBJECT);
            packageAssembly(AResult);
        }

        temp.content.add(objectNode);
    }

    /**
     * 递归解析结果节点描述部分并组装成结果对象
     */
    private static void packageAssembly(HtmlNodeTemp aResult) {
        if (ObjUtil.isNull(aResult.action)) {
            return;
        }

        if (NodeConstants.OBJECT.equals(aResult.lastType)) {
            structuralAnalysisMap(aResult);
        } else {
            for (Element element : aResult.arr) {
                aResult.obj = element;
                structuralAnalysisMap(aResult);
            }
        }

        if (!aResult.action.has(Constants.ELEMENT)) {
            return;
        }

        aResult.action = aResult.action.path(Constants.ELEMENT);

        aResult.next();

        packageAssembly(aResult);
    }


    /**
     * 结果解析并填充 JsonObject 对象属性：属性值
     */
    private static void structuralAnalysisMap(HtmlNodeTemp aResult) {

        if (!aResult.action.has(Constants.ELEMENT_TYPE) || !aResult.action.has(Constants.ELEMENT_VALUE)) {
            return;
        }

        String elementType = aResult.action.get(Constants.ELEMENT_TYPE).asText();
        String elementValue = aResult.action.get(Constants.ELEMENT_VALUE).asText();

        switch (elementType) {
            case NodeConstants.ID:
                aResult.obj = aResult.obj.getElementById(elementValue);
                results2Json(aResult);
                aResult.lastType = NodeConstants.OBJECT;
                break;
            case NodeConstants.CLASS:
                if (aResult.action.has(Constants.LEAF_INDEX)) {
                    int anInt = aResult.action.get(Constants.LEAF_INDEX).asInt();
                    try {
                        aResult.obj = aResult.obj.getElementsByClass(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }
                    results2Json(aResult);
                    aResult.lastType = NodeConstants.OBJECT;

                } else {
                    aResult.arr = aResult.obj.getElementsByClass(elementValue);
                    NodeFilter.nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = NodeConstants.ARRAY;
                    aResult.addChildNodes(aResult.arr);
                }
                break;

            case NodeConstants.TAGE:
                if (aResult.action.has(Constants.LEAF_INDEX)) {
                    int anInt = aResult.action.get(Constants.LEAF_INDEX).asInt();

                    try {
                        aResult.obj = aResult.obj.getElementsByTag(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(aResult);

                    aResult.lastType = NodeConstants.OBJECT;

                } else {
                    aResult.arr = aResult.obj.getElementsByTag(elementValue);
                    NodeFilter.nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = NodeConstants.ARRAY;
                    aResult.addChildNodes(aResult.arr);
                }
                break;
            case NodeConstants.CONTENT:
                results2Json(aResult);
                break;
        }
    }

    /**
     * 将结果组装为Json对象
     */
    private static void results2Json(HtmlNodeTemp aResult) {
        if (!aResult.action.has(Constants.IS_LEAF) || !aResult.action.has(Constants.TARGET_KEY)) {
            return;
        }

        String target = aResult.action.get(Constants.TARGET_KEY).asText();

        if (target.equals(NodeConstants.TEXT)) {
            String text = aResult.obj.text();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(text, aResult.action));
        } else if (target.equals(NodeConstants.HTML)) {
            String html = aResult.obj.html();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(html, aResult.action));
        } else {
            String attribute = aResult.obj.attr(target);
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(attribute, aResult.action));
        }
    }
}
