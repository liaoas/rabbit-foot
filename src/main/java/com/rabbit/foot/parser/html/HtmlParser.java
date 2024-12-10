package com.rabbit.foot.parser.html;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.constant.NodeConstants;
import com.rabbit.foot.network.HttpAsk;
import com.rabbit.foot.parser.BaseParser;
import com.rabbit.foot.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * HTML 爬虫解析器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-16
 */
public class HtmlParser<T> extends BaseParser implements Parser<T> {

    /**
     * 爬虫执行
     */
    @Override
    public List<T> execute(JsonNode activeRes) {
        // 获取动作
        super.activeRes = activeRes;

        if (ObjUtil.isNull(activeRes)) {
            return null;
        }

        // 爬取目标网站内容
        requestWebPage(activeRes);

        if (ObjUtil.isNull(this.webDocument)) {
            return null;
        }

        // 解析 HTML 节点
        ArrayNode arrayNode = elementResolver(activeRes);

        return convert(activeRes, arrayNode);
    }

    /**
     * 根据爬虫行为解析HTML
     *
     * @param resolverAction 解析动作
     * @return 爬取结果
     */
    private ArrayNode elementResolver(JsonNode resolverAction) {

        if (ObjUtil.isNull(resolverAction)) {
            return null;
        }

        HtmlNodeTemp temp = new HtmlNodeTemp(resolverAction, this.webDocument);

        resolver(temp);

        if (temp.content != null && temp.content.size() <= 0) {
            throw new RuntimeException("抓取目标网站失败");
        }

        return temp.content;
    }

    /**
     * 将结果转换为实体类对象
     *
     * @param action    动作
     * @param arrayNode 结果集合
     * @return T
     */
    private List<T> convert(JsonNode action, ArrayNode arrayNode) {
        JsonNode configObj = action.path(Constants.CONFIG);

        String javaType = configObj.get(Constants.JAVA_TYPE).asText();
        try {

            Class<?> Clazz = Class.forName(javaType);

            if (Clazz.getName().equals(String.class.getName())) {
                List<String> value = IntStream.range(0, arrayNode.size()).mapToObj(index -> arrayNode.get(index).get("value").asText()).collect(Collectors.toList());
                @SuppressWarnings("unchecked") List<T> newList = (List<T>) value;
                return newList;
            }

            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readerForListOf(Clazz).readValue(arrayNode.toString());
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据指定的爬虫行为抓取目标内容页面
     *
     * @param httpAction http 执行动作
     */
    private void requestWebPage(JsonNode httpAction) {

        JsonNode siteObj = httpAction.path(Constants.SITE);

        HttpAsk<String> httpAsk = new HttpAsk<>(siteObj);

        String content = httpAsk.execute();

        if (StrUtil.isEmpty(content)) {
            return;
        }
        this.webDocument = Jsoup.parse(content);
    }


    /**
     * 递归解析HTML节点至结果节点->packageAssembly()
     *
     * @param temp 存储爬虫解析参数
     */
    private void resolver(HtmlNodeTemp temp) {
        if (ObjUtil.isNull(temp.action)) {
            return;
        }

        if (NodeConstants.OBJECT.equals(temp.lastType)) {
            getElement(temp);
        } else {
            for (Element element : temp.arr) {
                temp.obj = element;
                getElement(temp);
            }
        }

        if (!temp.action.has(Constants.ELEMENT)) {
            return;
        }

        // 下一次动作
        temp.action = temp.action.path(Constants.ELEMENT);

        temp.next();

        // 递归下一次
        resolver(temp);
    }


    /**
     * 根绝爬虫描述，获取节点
     *
     * @param temp 存储爬虫解析参数
     */
    private void getElement(HtmlNodeTemp temp) {

        String elementType = temp.action.get(Constants.ELEMENT_TYPE).asText();

        String elementValue = temp.action.get(Constants.ELEMENT_VALUE).asText();

        switch (elementType) {
            case NodeConstants.ID:
                ElementIDParser.parser(temp, elementValue);
                break;
            case NodeConstants.CLASS:
                ElementClassParser.parser(temp, elementValue);
                break;
            case NodeConstants.TAGE:
                ElementTageParser.parser(temp, elementValue);
                break;
            case NodeConstants.RESULT:
                // 判断结果伪动作，标记开始组装结果
                ElementResultParser.parser(temp);
                break;
            default:
                ElementIndexParser.parser(temp, elementValue);
        }

    }

    /**
     * 递归解析结果节点描述部分并组装成结果对象
     */
    private void packageAssembly(HtmlNodeTemp aResult) {
        if (ObjUtil.isNull(aResult.action)) {
            return;
        }

        if (NodeConstants.OBJECT.equals(aResult.lastType)) {
            structuralAnalysisMap(aResult);
        } else {
            for (Element element : aResult.arr) {
                aResult.obj = element;
                structuralAnalysisMap(aResult);
            }
        }

        if (!aResult.action.has(Constants.ELEMENT)) {
            return;
        }

        aResult.action = aResult.action.path(Constants.ELEMENT);

        aResult.next();

        packageAssembly(aResult);
    }


    /**
     * 结果解析并填充 JsonObject 对象属性：属性值
     */
    private void structuralAnalysisMap(HtmlNodeTemp aResult) {

        if (!aResult.action.has(Constants.ELEMENT_TYPE) || !aResult.action.has(Constants.ELEMENT_VALUE)) {
            return;
        }

        String elementType = aResult.action.get(Constants.ELEMENT_TYPE).asText();
        String elementValue = aResult.action.get(Constants.ELEMENT_VALUE).asText();

        switch (elementType) {
            case NodeConstants.ID:
                aResult.obj = aResult.obj.getElementById(elementValue);
                results2Json(aResult);
                aResult.lastType = NodeConstants.OBJECT;
                break;
            case NodeConstants.CLASS:
                if (aResult.action.has(Constants.LEAF_INDEX)) {
                    int anInt = aResult.action.get(Constants.LEAF_INDEX).asInt();
                    try {
                        aResult.obj = aResult.obj.getElementsByClass(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }
                    results2Json(aResult);
                    aResult.lastType = NodeConstants.OBJECT;

                } else {
                    aResult.arr = aResult.obj.getElementsByClass(elementValue);
                    nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = NodeConstants.ARRAY;
                    aResult.addChildNodes(aResult.arr);
                }
                break;

            case NodeConstants.TAGE:
                if (aResult.action.has(Constants.LEAF_INDEX)) {
                    int anInt = aResult.action.get(Constants.LEAF_INDEX).asInt();

                    try {
                        aResult.obj = aResult.obj.getElementsByTag(elementValue).get(anInt);
                    } catch (Exception e) {
                        return;
                    }

                    results2Json(aResult);

                    aResult.lastType = NodeConstants.OBJECT;

                } else {
                    aResult.arr = aResult.obj.getElementsByTag(elementValue);
                    nodeFiltering(aResult.action, aResult.arr);
                    aResult.lastType = NodeConstants.ARRAY;
                    aResult.addChildNodes(aResult.arr);
                }
                break;
            case NodeConstants.CONTENT:
                results2Json(aResult);
                break;
        }
    }

    /**
     * 将结果组装为Json对象
     */
    private void results2Json(HtmlNodeTemp aResult) {
        if (!aResult.action.has(Constants.IS_LEAF) || !aResult.action.has(Constants.TARGET_KEY)) {
            return;
        }

        String target = aResult.action.get(Constants.TARGET_KEY).asText();

        if (target.equals(NodeConstants.TEXT)) {
            String text = aResult.obj.text();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), interceptors(text, aResult.action));
        } else if (target.equals(NodeConstants.HTML)) {
            String html = aResult.obj.html();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), interceptors(html, aResult.action));
        } else {
            String attribute = aResult.obj.attr(target);
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), interceptors(attribute, aResult.action));
        }
    }
}
