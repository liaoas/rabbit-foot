package com.rabbit.foot.handle;

import com.rabbit.foot.constant.NodeConstants;
import com.rabbit.foot.convert.HtmlResults2Json;
import com.rabbit.foot.html.NodeTemp;

/**
 * 用于根据ID来获取html节点元素
 *
 * @author LiAo
 * @since 2024/12/10
 */
public class HtmlIdHandle {

    public static void handle(NodeTemp temp, String id) {
        temp.obj = temp.obj.getElementById(id);
        temp.lastType = NodeConstants.OBJECT;
    }

    public static void formatResult(NodeTemp nodeTemp, String idName){
        nodeTemp.obj = nodeTemp.obj.getElementById(idName);
        HtmlResults2Json.results2Json(nodeTemp);
        nodeTemp.lastType = NodeConstants.OBJECT;
    }
}
