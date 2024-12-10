package com.rabbit.foot.parser.html;

import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.convert.HtmlResults2Json;

/**
 * 用于根据ID来获取html节点元素
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class HtmlIdHandle {

    public static void handle(HtmlNodeTemp temp, String id) {
        temp.obj = temp.obj.getElementById(id);
        temp.lastType = NodeConstants.OBJECT;
    }

    public static void formatResult(HtmlNodeTemp nodeTemp, String idName){
        nodeTemp.obj = nodeTemp.obj.getElementById(idName);
        HtmlResults2Json.results2Json(nodeTemp);
        nodeTemp.lastType = NodeConstants.OBJECT;
    }
}
