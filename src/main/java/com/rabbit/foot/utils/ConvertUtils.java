package com.rabbit.foot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * 对象格式转换工具类
 * </p>
 *
 * @author LiAo
 * @since 2023-08-19
 */
public class ConvertUtils {

    /**
     * JsonNode 转为 Map<String, Object>
     *
     * @param node 要转换的数据
     * @return 转换后的
     */
    public static Map<String, String> jsonNodeToMapStrStr(JsonNode node) {
        Map<String, String> map = new HashMap<>();
        if (node != null && node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                map.put(entry.getKey(), entry.getValue().asText());
            }
        }
        return map;
    }


    /**
     * JsonNode 转为 JsonString 字符串
     *
     * @param jsonNode 要转换的数据
     * @return 转换后的
     */
    public static String jsonNodeToStr(JsonNode jsonNode) {

        return jsonNode.toString();
    }

    /**
     * JsonStr 转为 JsonNode
     *
     * @param jsonString String
     * @return JsonNode
     */
    public static JsonNode jsonStrToJsonNode(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode;
    }
}
