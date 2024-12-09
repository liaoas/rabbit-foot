package com.rabbit.foot.core.resolver.html;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.constant.NodeConstants;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 存储HTML解析时的临时参数
 *
 * @author LiAo
 * @since 2024/12/9
 */
public class HtmlTemp {

    // 存储爬虫结果内容集合
    ArrayNode content;
    // 存储单条爬虫结果
    ObjectNode contentTemp;
    // 爬虫行为
    JsonNode action;
    // 存储多个节点
    Elements arr;
    // 存储单个节点 | 要解析的节点
    Element obj;
    // 上一次节点类型；
    String lastType;
    // 存储Array类型的子节点集合
    Elements childNode = new Elements();


    public HtmlTemp(JsonNode action, Element webDocument) {

        JsonNode node = action.path(Constants.RESOLVER_ACTION);
        node = node.path(Constants.ELEMENT);

        this.content = new JsonMapper().createArrayNode();
        this.action = node;
        this.arr = null;
        this.obj = webDocument;
        this.lastType = NodeConstants.OBJECT;
    }


    public HtmlTemp(ObjectNode contentTemp, JsonNode action, Elements arr, Element obj, String lastType) {
        this.contentTemp = contentTemp;
        this.action = action;
        this.arr = arr;
        this.obj = obj;
        this.lastType = lastType;
    }

    /**
     * 克隆节点，作为下一次递归指针
     */
    public void next() {
        if (this.childNode.isEmpty()) {
            return;
        }

        this.arr = this.childNode.clone();
        this.clearChildNode();
        this.lastType = NodeConstants.ARRAY;
    }

    public void addChildNodes(Elements elements) {
        this.childNode.addAll(elements);
    }

    private void clearChildNode() {
        this.childNode.clear();
    }
}
