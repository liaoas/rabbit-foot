package com.liao.core.spider.html;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
        JsonObject spiderActionJson = ActionResourcesJson.getSpiderActionJson(spiderName);

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
    private void captureWebContent(JsonObject httpAction) {

        JsonObject siteObj = httpAction.get("site").getAsJsonObject();
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
    private JsonArray webElementResolver(JsonObject resolverAction) {
        if (ObjUtil.isNull(resolverAction)) {
            return null;
        }

        JsonObject action = resolverAction.get("resolver-action").getAsJsonObject();

        return null;
    }

    /**
     * 递归解析内容
     *
     * @param content  存储内容
     * @param action   动作
     * @param element  爬虫进度指针
     * @param elements 爬虫进度指针
     */
    private void resolver(JsonArray content, JsonObject action, Element element, Elements elements) {
        if (ObjUtil.isNull(action)) {
            return;
        }
    }
}
