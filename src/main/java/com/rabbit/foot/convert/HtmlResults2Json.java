package com.rabbit.foot.convert;

import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.constant.NodeConstants;
import com.rabbit.foot.parser.html.HtmlNodeTemp;
import com.rabbit.foot.parser.html.NodeFilter;

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
    public static void results2Json(HtmlNodeTemp aResult) {
        if (!aResult.action.has(Constants.IS_LEAF) || !aResult.action.has(Constants.TARGET_KEY)) {
            return;
        }

        String target = aResult.action.get(Constants.TARGET_KEY).asText();

        if (target.equals(NodeConstants.TEXT)) {
            String text = aResult.obj.text();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(text, aResult.action));
        } else if (target.equals(NodeConstants.HTML)) {
            String html = aResult.obj.html();
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(html, aResult.action));
        } else {
            String attribute = aResult.obj.attr(target);
            aResult.contentTemp.put(aResult.action.get(Constants.RESULT_KEY).asText(), NodeFilter.interceptors(attribute, aResult.action));
        }
    }
}
