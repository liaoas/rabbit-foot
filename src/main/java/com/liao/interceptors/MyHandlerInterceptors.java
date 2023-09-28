package com.liao.interceptors;

/**
 * <p>
 * 用于测试处理器
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public class MyHandlerInterceptors implements HandlerInterceptors<String> {
    /**
     * 前置处理器
     *
     * @param handler 需要处理的参数
     * @param prefix  需要处理的参数前缀
     */
    @Override
    public String handle(String handler, String prefix, String suffix) {
        if (handler == null) {
            return null;
        }

        return prefix + handler + suffix;
    }


}
