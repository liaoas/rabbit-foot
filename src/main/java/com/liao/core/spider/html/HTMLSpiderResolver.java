package com.liao.core.spider.html;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        ArrayNode arrayNode = webElementResolver(spiderActionJson);
        System.out.println();
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
    private ArrayNode webElementResolver(JsonNode resolverAction) {
        if (ObjUtil.isNull(resolverAction)) {
            return null;
        }

        JsonNode node = resolverAction.path("resolver-action");
        node = node.path("element");
        JsonMapper resultMap = new JsonMapper();

        ArrayNode arrayNode = resultMap.createArrayNode();

        // 递归解析
        resolver(arrayNode, null, node, null, this.document, "obj");

        return arrayNode;
    }

    /**
     * 递归解析内容
     *
     * @param content     用于存储爬虫的结果内容
     * @param contentTemp 用于临时存储不同节点下的结果集
     * @param action      动作
     * @param arr         爬虫进度指针 多节点
     * @param obj         爬虫进度指针 单节点
     * @param lastType    上次节点类型
     */
    private void resolver(ArrayNode content, ObjectNode contentTemp, JsonNode action, Elements arr, Element obj, String lastType) {
        if (ObjUtil.isNull(action)) {
            return;
        }

        String elementType = action.get("element-type").asText();

        String elementValue = action.get("element-value").asText();

        if ("obj".equals(lastType)) {
            switch (elementType) {
                case "id":
                    obj = obj.getElementById(elementValue);
                    lastType = "obj";
                    break;
                case "class":
                    arr = obj.getElementsByClass(elementValue);
                    lastType = "arr";
                    break;
            }
        } else {
            for (Element element : arr) {
                switch (elementType) {
                    case "id":
                        if (action.has("leaf-index")) {
                            obj = element.getElementById(elementValue);
                            lastType = "obj";
                        } else {
                            obj = element.getElementById(elementValue);
                            lastType = "arr";
                        }
                        break;
                    case "class":
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByClass(elementValue).get(anInt);
                            lastType = "obj";
                        } else {
                            arr = element.getElementsByClass(elementValue);
                            lastType = "arr";
                        }
                        break;

                    case "tage":
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByTag(elementValue).get(anInt);
                            lastType = "obj";
                        } else {
                            arr = element.getElementsByTag(elementValue);
                            lastType = "arr";
                        }

                        break;
                    case "result":
                        // 是否开始分开组装结果
                        if (!action.has("result-element")) {
                            break;
                        }

                        ArrayNode arrayNode = action.withArray("result-element");
                        ObjectNode objectNode = new ObjectMapper().createObjectNode();

                        for (JsonNode jsonNode : arrayNode) {
                            packageAssembly(objectNode, jsonNode, null, element, "obj");
                        }

                        content.add(objectNode);
                        break;
                }

            }
        }

        if (!action.has("element")) {
            return;
        }

        action = action.path("element");

        resolver(content, contentTemp, action, arr, obj, lastType);
    }

    private void packageAssembly(ObjectNode contentTemp, JsonNode action, Elements arr, Element obj, String lastType) {
        if (ObjUtil.isNull(action)) {
            return;
        }

        String elementType = action.get("element-type").asText();

        String elementValue = action.get("element-value").asText();

        if ("obj".equals(lastType)) {
            switch (elementType) {
                case "id":
                    // 判断是否是叶子|结果节点
                    if (action.has("leaf-index")) {
                        obj = obj.getElementById(elementValue);
                        if (action.has("is-leaf")) {

                            if (!action.has("target-key")) {
                                break;
                            }

                            String text = action.get("target-key").asText();
                            if (text.equals("text")) {
                                contentTemp.put(action.get("result-key").asText(), obj.text());

                            }

                            if (text.equals("src")) {
                                contentTemp.put(action.get("result-key").asText(), obj.attr("src"));

                            }
                        }
                        lastType = "obj";


                    } else {
                        obj = obj.getElementById(elementValue);
                        lastType = "obj";
                    }
                    break;
                case "class":
                    // 判断是否是叶子|结果节点
                    if (action.has("leaf-index")) {
                        int anInt = action.get("leaf-index").asInt();
                        obj = obj.getElementsByClass(elementValue).get(anInt);
                        if (action.has("is-leaf")) {

                            if (!action.has("target-key")) {

                            }

                            String text = action.get("target-key").asText();
                            if (text.equals("text")) {
                                contentTemp.put(action.get("result-key").asText(), obj.text());

                            }

                            if (text.equals("src")) {
                                contentTemp.put(action.get("result-key").asText(), obj.attr("src"));

                            }
                        }
                        lastType = "obj";


                    } else {
                        arr = obj.getElementsByClass(elementValue);
                        lastType = "arr";
                    }
                    break;
                case "tage":
                    if (action.has("leaf-index")) {
                        int anInt = action.get("leaf-index").asInt();
                        obj = obj.getElementsByTag(elementValue).get(anInt);
                        if (action.has("is-leaf")) {
                            String text = action.get("target-key").asText();

                            if (!action.has("target-key")) {
                                break;
                            }

                            if (text.equals("text")) {
                                contentTemp.put(action.get("result-key").asText(), obj.text());

                            }

                            if (text.equals("src")) {
                                contentTemp.put(action.get("result-key").asText(), obj.attr("src"));
                            }

                        }
                        lastType = "obj";
                    } else {
                        arr = obj.getElementsByTag(elementValue);
                        lastType = "arr";
                    }
                    break;
            }
        } else {
            for (Element element : arr) {
                switch (elementType) {
                    case "id":
                        // 判断是否是叶子|结果节点
                        if (action.has("leaf-index")) {
                            obj = element.getElementById(elementValue);
                            if (action.has("is-leaf")) {

                                if (!action.has("target-key")) {
                                    break;
                                }

                                String text = action.get("target-key").asText();
                                if (text.equals("text")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.text());

                                }

                                if (text.equals("src")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.attr("src"));
                                }
                            }
                            lastType = "obj";


                        } else {
                            obj = element.getElementById(elementValue);
                            lastType = "obj";
                        }
                        break;
                    case "class":

                        // 判断是否是叶子|结果节点
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByClass(elementValue).get(anInt);
                            if (action.has("is-leaf")) {

                                if (!action.has("target-key")) {
                                    break;
                                }

                                String text = action.get("target-key").asText();
                                if (text.equals("text")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.text());

                                }

                                if (text.equals("src")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.attr("src"));

                                }
                            }
                            lastType = "obj";


                        } else {
                            arr = element.getElementsByClass(elementValue);
                            lastType = "arr";
                        }
                        break;
                    case "tage":
                        if (action.has("leaf-index")) {
                            int anInt = action.get("leaf-index").asInt();
                            obj = element.getElementsByTag(elementValue).get(anInt);
                            if (action.has("is-leaf")) {
                                String text = action.get("target-key").asText();

                                if (!action.has("target-key")) {
                                    break;
                                }

                                if (text.equals("text")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.text());

                                }

                                if (text.equals("src")) {
                                    contentTemp.put(action.get("result-key").asText(), obj.attr("src"));

                                }

                            }
                            lastType = "obj";
                        } else {
                            arr = element.getElementsByTag(elementValue);
                            lastType = "arr";
                        }
                        break;
                }
            }
        }

        if (!action.has("element")) {
            return;
        }

        action = action.path("element");

        packageAssembly(contentTemp, action, arr, obj, lastType);
    }

    private ObjectNode objectNodeCopy(ObjectNode nodes) {
        JsonMapper jsonMapper = new JsonMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = jsonMapper.readTree(nodes.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (jsonNode instanceof ObjectNode) {
            return (ObjectNode) jsonNode;
        }

        return null;
    }


}
