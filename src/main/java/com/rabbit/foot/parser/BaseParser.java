package com.rabbit.foot.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.loader.ActionResources;
import com.rabbit.foot.interceptors.HandlerInterceptors;
import com.rabbit.foot.utils.BeanUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * <p>
 * 爬虫基础类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-20
 */
public abstract class BaseParser extends ActionResources {

    /**
     * 爬取的Web节点
     */
    protected Document webDocument;
}
