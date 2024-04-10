package com.rabbit.foot.core.resources;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.enums.ReptileType;
import com.rabbit.foot.common.utils.ConvertUtils;

import java.net.URL;

/**
 * <p>
 * 爬虫动作描述资源类
 * </p>
 *
 * @author LiAo
 * @since 2023-08-03
 */
public abstract class ActionResources {

    public URL url;

    public String jsonString;

    public String spiderName;

    public ReptileType spiderType;

    // 当前使用的爬虫动作
    protected JsonNode activeRes;

    /**
     * 获取指定名称的爬虫描述
     */
    public void getSpiderActionConfigByName(String... params) {
        if (StrUtil.isEmpty(spiderName)) {
            return;
        }

        getSpiderActionConfig(Resources.getObjectNode(), params);
    }

    /**
     * 获取指定名称的爬虫描述
     */
    public void getSpiderActionConfigByUrl(String... params) {
        if (ObjUtil.isEmpty(url)) {
            return;
        }

        getSpiderActionConfig(Resources.getObjectNode(url), params);
    }

    /**
     * 获取指定名称的爬虫描述
     */
    public void getSpiderActionConfigByStr(String... params) {
        if (ObjUtil.isEmpty(jsonString)) {
            return;
        }

        getSpiderActionConfig(Resources.getObjectNode(jsonString), params);
    }

    /**
     * 获取指定名称的爬虫动作
     *
     * @param objectNode 爬虫资源
     * @param params     爬虫http填充
     */
    private void getSpiderActionConfig(ObjectNode objectNode, String... params) {
        ArrayNode books = objectNode.withArray(Constants.BOOKS);

        if (books.size() <= 0) {
            throw new IllegalArgumentException("spider-action-test.json 爬虫资源为空");
        }

        for (JsonNode book : books) {
            if (spiderName.equals(book.get(Constants.NAME).asText()) &&
                    spiderType.value.equals(book.get(Constants.SPIDER_TYPE).asText())) {
                activeRes = book;
            }
        }

        if (ObjUtil.isNull(activeRes)) {
            throw new RuntimeException("spiderName：" + spiderName + ", spiderType: " + spiderType.value + " 资源获取为空");
        }

        // 填充请求参数
        httpParamBuild(params);
    }

    /**
     * 根据通配符填充http参数
     *
     * @param params 参数
     */
    public void httpParamBuild(String... params) {

        if (params == null || params.length == 0) {
            return;
        }

        String activeResStr = ConvertUtils.jsonNodeToStr(activeRes);

        for (int i = 0; i < params.length; i++) {
            String matchCharacter = "{params[" + i + "]}";
            activeResStr = activeResStr.replace(matchCharacter, params[i]);
        }

        activeRes = ConvertUtils.jsonStrToJsonNode(activeResStr);
    }


}
