package com.liao.core;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

/**
 * <p>
 * 爬虫动作描述资源类
 * </p>
 *
 * @author LiAo
 * @since 2023-08-03
 */
@Slf4j
public class ActionResourcesJson {

    /**
     * 获取指定名称的爬虫描述
     *
     * @param name 爬虫名称
     * @return 爬虫描述 Json
     */
    public static JsonNode getSpiderActionJson(String name) {
        if (StrUtil.isEmpty(name)) {
            return null;
        }

        ObjectNode node = readSpiderActionJson();

        if (ObjUtil.isNull(node)) {
            return null;
        }


        ArrayNode books = node.withArray("books");

        for (JsonNode book : books) {
            if (name.equals(book.get("name").asText())) {
                return book;
            }
        }

        return null;
    }

    /**
     * 读取爬虫动作的资源文件
     */
    private static ObjectNode readSpiderActionJson() {

        ObjectMapper mapper = new ObjectMapper();

        // 通过 ClassLoader 获取资源文件的输入流
        URL resource = ActionResourcesJson.class.getClassLoader().getResource("spider-action-test.json");
        ObjectNode objectNode = null;
        try {
            objectNode = mapper.readValue(resource, ObjectNode.class);
        } catch (IOException e) {
            log.error("爬虫资源解析失败，请检数据是否合法");
        }

        if (ObjUtil.isNull(objectNode)) {
            throw new IllegalArgumentException("爬虫动作描述资源缺失");
        }

        return objectNode;
    }

}
