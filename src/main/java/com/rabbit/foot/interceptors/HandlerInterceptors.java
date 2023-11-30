package com.rabbit.foot.interceptors;

/**
 * <p>
 * 拦截器接口
 * 实现该接口，并在爬虫动作上指定实现了该接口的处理类
 * 用于在爬虫结果组装成对象之前对爬虫的结果进行处理
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public interface HandlerInterceptors<T> {

    /**
     * 拦截器处理
     *
     * @param handler 需要处理的参数
     * @param prefix  需要处理的参数前缀
     * @param suffix  需要处理的参数后缀
     */
    T handle(T handler, String prefix, String suffix);
}
