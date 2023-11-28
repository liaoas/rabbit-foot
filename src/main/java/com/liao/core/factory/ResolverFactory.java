package com.liao.core.factory;

import com.liao.core.ActionResources;
import com.liao.core.spider.Resolver;
import com.liao.utils.BeanUtils;

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
public class ResolverFactory<T> extends ActionResources implements Resolver<T> {


    /**
     * 爬虫执行
     *
     * @param spiderName 爬虫资源名称
     * @return 爬虫结果
     */
    @Override
    public List<T> execute(String spiderName, String... params) {
        getSpiderActionConfig(spiderName);
        Resolver<T> bean = (Resolver<T>) BeanUtils.getBean(activeRes);
        return bean.execute(spiderName, params);
    }
}
