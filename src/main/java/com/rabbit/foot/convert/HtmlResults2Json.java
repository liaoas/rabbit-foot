package com.rabbit.foot.convert;

import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.html.NodeTemp;
import com.rabbit.foot.filter.NodeFilter;

/**
 * 将解析的Html结果转化为Json
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class HtmlResults2Json {

    /**
     * 将结果组装为Json对象
     */
    public static void results2Json(NodeTemp nodeTemp) {
        if (!nodeTemp.action.has(Constants.IS_LEAF) || !nodeTemp.action.has(Constants.TARGET_KEY)) {
            return;
        }

        String target = nodeTemp.action.get(Constants.TARGET_KEY).asText();

        if (target.equals(NodeConstants.TEXT)) {
            String text = nodeTemp.obj.text();
            nodeTemp.contentTemp.put(nodeTemp.action.get(Constants.RESULT_KEY).asText(), NodeFilter.filter(text, nodeTemp.action));
        } else if (target.equals(NodeConstants.HTML)) {
            String html = nodeTemp.obj.html();
            nodeTemp.contentTemp.put(nodeTemp.action.get(Constants.RESULT_KEY).asText(), NodeFilter.filter(html, nodeTemp.action));
        } else {
            String attribute = nodeTemp.obj.attr(target);
            nodeTemp.contentTemp.put(nodeTemp.action.get(Constants.RESULT_KEY).asText(), NodeFilter.filter(attribute, nodeTemp.action));
        }
    }
}
