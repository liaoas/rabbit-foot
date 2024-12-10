package com.rabbit.foot.parser.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.convert.HtmlResults2Json;
import com.rabbit.foot.utils.ObjUtil;
import org.jsoup.nodes.Element;

/**
 * 解析内容节点，并且将内容组装成指定的类型
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class HtmlResultHandle {

    public static void handle(HtmlNodeTemp temp) {
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
    private static void structuralAnalysisMap(HtmlNodeTemp nodeTemp) {

        if (!nodeTemp.action.has(Constants.ELEMENT_TYPE) || !nodeTemp.action.has(Constants.ELEMENT_VALUE)) {
            return;
        }

        String type = nodeTemp.action.get(Constants.ELEMENT_TYPE).asText();
        String value = nodeTemp.action.get(Constants.ELEMENT_VALUE).asText();

        switch (type) {
            case NodeConstants.ID:
                HtmlIdHandle.formatResult(nodeTemp, value);
                break;
            case NodeConstants.CLASS:
                HtmlClassHandle.formatResult(nodeTemp, value);
                break;
            case NodeConstants.TAGE:
                HtmlTageHandle.formatResult(nodeTemp, value);
                break;
            case NodeConstants.CONTENT:
                HtmlResults2Json.results2Json(nodeTemp);
                break;
        }
    }
}
