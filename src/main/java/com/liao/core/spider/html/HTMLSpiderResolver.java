package com.liao.core.spider.html;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonArray;
import com.liao.core.ActionResourcesJson;
import com.liao.network.HttpAsk;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <p>
 * HTML 爬虫解析器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-16
 */
public class HTMLSpiderResolver {

    private Document document;

    /**
     * 爬虫执行
     *
     * @param spiderName 爬虫动作名称
     */
    public void execute(String spiderName) {
        // 获取动作
        JsonNode spiderActionJson = ActionResourcesJson.getSpiderActionJson(spiderName);

        if (ObjUtil.isNull(spiderActionJson)) {
            return;
        }

        // 爬取目标网站内容
        captureWebContent(spiderActionJson);

        if (ObjUtil.isNull(this.document)) {
            return;
        }

        // 解析 HTML 节点
        JsonArray jsonObject = webElementResolver(spiderActionJson);

    }

    /**
     * 根据指定的爬虫动作抓取目标内容
     *
     * @param httpAction http 执行动作
     */
    private void captureWebContent(JsonNode httpAction) {

        JsonNode siteObj = httpAction.path("site");

        HttpAsk<String> httpAsk = new HttpAsk<>(siteObj);

        String content = httpAsk.execute();

        if (StrUtil.isEmpty(content)) {
            return;
        }
        this.document = Jsoup.parse(content);
    }

    /**
     * 根据爬虫动作解析HTML
     *
     * @param resolverAction 解析动作
     * @return 爬取结果
     */
    private JsonArray webElementResolver(JsonNode resolverAction) {
        if (ObjUtil.isNull(resolverAction)) {
            return null;
        }

        JsonNode node = resolverAction.path("resolver-action");
        JsonArray resultArray = new JsonArray();

        // 递归解析
        resolver(resultArray, node, null, document);

        return null;
    }

    /**
     * 递归解析内容
     *
     * @param content 用于爬虫的结果内容
     * @param action  动作
     * @param array   爬虫进度指针 多节点
     * @param obj     爬虫进度指针 单节点
     */
    private void resolver(JsonArray content, JsonNode action, Elements array, Element obj) {
        if (ObjUtil.isNull(action)) {
            return;
        }

        JsonNode elementNode = action.path("element");

        System.out.println(obj.html());
    }
}
