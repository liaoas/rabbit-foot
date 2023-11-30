package com.rabbit.foot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * JsonNode 转为 Map<String, Object>
     *
     * @param jsonNode 要转换的数据
     * @return 转换后的
     */
    public static Map<String, Object> jsonNodeToMapStrObj(JsonNode jsonNode) {

        return MAPPER.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * JsonNode 转为 Map<String, String>
     *
     * @param jsonNode 要转换的数据
     * @return 转换后的
     */
    public static Map<String, String> jsonNodeToMapStrStr(JsonNode jsonNode) {

        return MAPPER.convertValue(jsonNode, new TypeReference<Map<String, String>>() {
        });
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
