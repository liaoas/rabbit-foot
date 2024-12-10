package com.rabbit.foot.parser.html;

import com.rabbit.foot.common.constant.NodeConstants;

/**
 * 用于根据ID来获取html节点元素
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class ElementIDParser {

    public static void parser(HtmlNodeTemp temp, String id) {
        temp.obj = temp.obj.getElementById(id);
        temp.lastType = NodeConstants.OBJECT;
    }
}
