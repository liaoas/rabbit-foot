package com.rabbit.foot.core.factory;

import com.rabbit.foot.core.ActionResources;
import com.rabbit.foot.core.spider.Resolver;
import com.rabbit.foot.utils.BeanUtils;

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

    public ResolverFactory(String spiderName) {
        super.spiderName = spiderName;
        getSpiderActionConfigByName();
    }

    public ResolverFactory(URL url) {
        super.url = url;
        getSpiderActionConfigByUrl();
    }


    /**
     * 爬虫执行
     *
     * @param params 爬虫http请求参数
     * @return 爬虫结果
     */
    public List<T> execute(String... params) {
        Resolver<T> bean = (Resolver<T>) BeanUtils.getBean(activeRes);
        return bean.execute(activeRes);
    }
}
