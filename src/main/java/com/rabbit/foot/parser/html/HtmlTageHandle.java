package com.rabbit.foot.parser.html;

import com.rabbit.foot.constant.Constants;
import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.convert.HtmlResults2Json;
import com.rabbit.foot.filter.NodeFilter;

/**
 * 用于根据Tage来获取html节点元素
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class HtmlTageHandle {

    public static void handle(HtmlNodeTemp temp, String tageName) {
        if (temp.action.has(Constants.LEAF_INDEX)) {
            int anInt = temp.action.get(Constants.LEAF_INDEX).asInt();
            temp.obj = temp.obj.getElementsByTag(tageName).get(anInt);
            temp.lastType = NodeConstants.OBJECT;
        } else {
            temp.arr = temp.obj.getElementsByTag(tageName);
            temp.lastType = NodeConstants.ARRAY;
            NodeFilter.nodeFiltering(temp.action, temp.arr);
            temp.addChildNodes(temp.arr);
        }
    }

    public static void formatResult(HtmlNodeTemp nodeTemp, String tageName){
        if (nodeTemp.action.has(Constants.LEAF_INDEX)) {
            int anInt = nodeTemp.action.get(Constants.LEAF_INDEX).asInt();

            try {
                nodeTemp.obj = nodeTemp.obj.getElementsByTag(tageName).get(anInt);
            } catch (Exception e) {
                return;
            }
            HtmlResults2Json.results2Json(nodeTemp);
            nodeTemp.lastType = NodeConstants.OBJECT;
        } else {
            nodeTemp.arr = nodeTemp.obj.getElementsByTag(tageName);
            NodeFilter.nodeFiltering(nodeTemp.action, nodeTemp.arr);
            nodeTemp.lastType = NodeConstants.ARRAY;
            nodeTemp.addChildNodes(nodeTemp.arr);
        }
    }
}
