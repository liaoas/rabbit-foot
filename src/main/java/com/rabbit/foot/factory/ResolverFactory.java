package com.rabbit.foot.factory;

import com.rabbit.foot.common.enums.ReptileType;
import com.rabbit.foot.loader.ActionResources;
import com.rabbit.foot.parser.Parser;
import com.rabbit.foot.common.utils.BeanUtils;

import java.net.URL;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
@SuppressWarnings("unchecked")
public class ResolverFactory<T> extends ActionResources {

    private ResolverFactory() {
    }

    public ResolverFactory(URL url, String spiderName, ReptileType spiderType, String... params) {
        super.url = url;
        super.spiderName = spiderName;
        super.spiderType = spiderType;
        getSpiderActionConfigByUrl(params);
    }

    public ResolverFactory(String jsonStr, String spiderName, ReptileType spiderType, String... params) {
        super.jsonString = jsonStr;
        super.spiderName = spiderName;
        super.spiderType = spiderType;
        getSpiderActionConfigByStr(params);
    }


    /**
     * 爬虫执行
     *
     * @return 爬虫结果
     */
    public List<T> capture() {
        Parser<T> bean = (Parser<T>) BeanUtils.getBean(activeRes);
        return bean.execute(activeRes);
    }
}


