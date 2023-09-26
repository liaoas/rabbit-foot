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
public abstract class ActionResources {

    protected volatile ObjectNode objectNode = null;

    // 当前使用的爬虫动作
    protected JsonNode activeRes;

    /**
     * 获取指定名称的爬虫描述
     *
     * @param name 爬虫名称
     */
    public void getSpiderActionConfig(String name) {
        if (StrUtil.isEmpty(name)) {
            return;
        }

        // 验证是否加载资源
        isLoad();

        ArrayNode books = objectNode.withArray("books");

        if (books.size() <= 0) {
            throw new IllegalArgumentException("spider-action-test.json 爬虫资源为空");
        }

        for (JsonNode book : books) {
            if (name.equals(book.get("name").asText())) {
                activeRes = book;
            }
        }
    }

    /**
     * 是否加载爬虫动作
     */
    public void isLoad() {
        if (objectNode == null) {
            loadSpiderAction();
        }
    }

    /**
     * 加载爬虫动作
     */
    private void loadSpiderAction() {

        if (objectNode == null) {

            synchronized (ActionResources.class) {
                if (objectNode == null) {
                    ObjectMapper mapper = new ObjectMapper();

                    URL resource = null;

                    try {
                        resource = ActionResources.class.getClassLoader().getResource("spider-action-test.json");
                    } catch (Exception e) {
                        log.error("爬虫资源 spider-action-test.json 获取失败");
                    }

                    try {
                        objectNode = mapper.readValue(resource, ObjectNode.class);
                    } catch (IOException e) {
                        log.error("爬虫资源 spider-action-test.json 读取失败");
                    }

                    if (ObjUtil.isNull(objectNode)) {
                        throw new NullPointerException("爬虫资源 spider-action-test.json 读取为空");
                    }
                }
            }
        }
    }

}
