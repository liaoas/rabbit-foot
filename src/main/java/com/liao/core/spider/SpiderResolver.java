package com.liao.core.spider;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.liao.core.ActionResources;
import org.jsoup.nodes.Document;

/**
 * <p>
 * 爬虫解析抽象类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-20
 */
public abstract class SpiderResolver extends ActionResources {

    protected Document document;


}
