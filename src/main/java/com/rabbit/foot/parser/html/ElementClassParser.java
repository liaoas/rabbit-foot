package com.rabbit.foot.parser.html;

import com.rabbit.foot.common.constant.Constants;
import com.rabbit.foot.common.constant.NodeConstants;

/**
 * 用于根据Class来获取html节点元素
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class ElementClassParser {

    public static void parser(HtmlNodeTemp temp, String className) {
        if (temp.action.has(Constants.LEAF_INDEX)) {
            int anInt = temp.action.get(Constants.LEAF_INDEX).asInt();
            temp.obj = temp.obj.getElementsByClass(className).get(anInt);
            temp.lastType = NodeConstants.OBJECT;
        } else {
            temp.arr = temp.obj.getElementsByClass(className);
            temp.lastType = NodeConstants.ARRAY;
            NodeFilter.nodeFiltering(temp.action, temp.arr);
            temp.addChildNodes(temp.arr);
        }
    }
}
