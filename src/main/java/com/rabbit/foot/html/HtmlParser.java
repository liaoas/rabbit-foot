package com.rabbit.foot.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.handle.HtmlClassHandle;
import com.rabbit.foot.handle.HtmlIdHandle;
import com.rabbit.foot.handle.HtmlResultHandle;
import com.rabbit.foot.handle.HtmlTageHandle;
import com.rabbit.foot.network.HttpAsk;
import com.rabbit.foot.parser.BaseParser;
import com.rabbit.foot.parser.Parser;
import com.rabbit.foot.utils.ObjUtil;
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

        NodeTemp temp = new NodeTemp(resolverAction, this.webDocument);

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
                List<String> value = IntStream
                        .range(0, arrayNode.size())
                        .mapToObj(index -> arrayNode.get(index).get("value").asText())
                        .collect(Collectors.toList());

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

        if (ObjUtil.isEmpty(content)) {
            return;
        }
        this.webDocument = Jsoup.parse(content);
    }


    /**
     * 递归解析HTML节点至结果节点->packageAssembly()
     *
     * @param temp 存储爬虫解析参数
     */
    private void resolver(NodeTemp temp) {
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
    private void getElement(NodeTemp temp) {

        String type = temp.action.get(Constants.ELEMENT_TYPE).asText();

        String value = temp.action.get(Constants.ELEMENT_VALUE).asText();

        switch (type) {
            case NodeConstants.ID:
                HtmlIdHandle.handle(temp, value);
                break;
            case NodeConstants.TAGE:
                HtmlTageHandle.handle(temp, value);
                break;
            case NodeConstants.RESULT:
                // 判断结果伪动作，标记开始组装结果
                HtmlResultHandle.handle(temp);
                break;
            default:
                HtmlClassHandle.handle(temp, value);
        }

    }
}
