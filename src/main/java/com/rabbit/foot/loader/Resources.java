package com.rabbit.foot.loader;

import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.github.GitHubFileReader;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * 资源类，用于管理资源的加载，和资源的获取
 * </p>
 *
 * @author LiAo
 * @since 2023-12-01
 */
public class Resources {

    private static final Logger logger = Logger.getLogger(GitHubFileReader.class.getName());

    private static volatile ObjectNode objectNode = null;

    private static final String errorRule = "{\"rule\":\"error\"}";

    public static ObjectNode getObjectNode() {
        loadSpiderAction();
        return objectNode;
    }

    public static ObjectNode getObjectNode(URL url) {
        loadSpiderAction(url);
        return objectNode;
    }

    public static ObjectNode getObjectNode(String jsonStr) {
        loadSpiderAction(jsonStr);
        return objectNode;
    }


    /**
     * 加载爬虫行为的描述文件
     */
    private static void loadSpiderAction() {

        if (objectNode == null) {

            synchronized (ActionResources.class) {
                if (objectNode == null) {
                    ObjectMapper mapper = new ObjectMapper();

                    URL resource = null;

                    try {
                        resource = ActionResources.class.getClassLoader().getResource("spider-action-test.json");
                    } catch (Exception e) {
                        logger.warning("爬虫资源 spider-action-test.json 获取失败");
                    }

                    try {
                        objectNode = mapper.readValue(resource, ObjectNode.class);
                    } catch (IOException e) {
                        logger.warning("爬虫资源 spider-action-test.json 读取失败");
                    }

                    if (ObjUtil.isNull(objectNode)) {
                        throw new NullPointerException("爬虫资源 spider-action-test.json 读取为空");
                    }
                }
            }
        }
    }


    /**
     * 根据指定 URL 加载爬虫行为的描述文件
     *
     * @param url url
     */
    private static void loadSpiderAction(URL url) {


        synchronized (ActionResources.class) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                objectNode = mapper.readValue(url, ObjectNode.class);
            } catch (IOException e) {
                logger.warning("爬虫资源读取失败 -> :" + url.getUserInfo());
                try {
                    objectNode = mapper.readValue(errorRule, ObjectNode.class);
                } catch (JsonProcessingException ex) {
                    logger.warning("爬虫资源加载失败,设置默认值 -> :" + errorRule);
                }
            }

            if (ObjUtil.isNull(objectNode)) {
                throw new NullPointerException("爬虫资源 " + url.getUserInfo() + " 读取为空");
            }
        }
    }

    /**
     * 根据指定 json 字符串 加载爬虫行为的描述文件
     *
     * @param jsonStr 爬虫行行为描述
     */
    private static void loadSpiderAction(String jsonStr) {

        synchronized (ActionResources.class) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                objectNode = mapper.readValue(jsonStr, ObjectNode.class);
            } catch (IOException e) {
                logger.warning("爬虫资源加载失败 -> :" + jsonStr);
                try {
                    objectNode = mapper.readValue(errorRule, ObjectNode.class);
                } catch (JsonProcessingException ex) {
                    logger.warning("爬虫资源加载失败,设置默认值 -> :" + errorRule);
                }
            }

            if (ObjUtil.isNull(objectNode)) {
                throw new NullPointerException("爬虫资源加载失败");
            }
        }
    }

    /**
     * 获取爬虫集合的名称集合
     *
     * @return 名称集合
     */
    public static Set<String> getResourceNames() {

        if (objectNode == null) {
            return null;
        }

        ArrayNode books = objectNode.withArray(Constants.BOOKS);

        if (books.size() <= 0) {
            throw new IllegalArgumentException("spider-action-test.json 爬虫行为资源为空");
        }

        HashSet<String> names = new HashSet<>();

        for (JsonNode book : books) {
            String name = book.get(Constants.NAME).asText();
            names.add(name);
        }

        return names;
    }
}
