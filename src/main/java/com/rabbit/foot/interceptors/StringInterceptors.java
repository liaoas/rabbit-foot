package com.rabbit.foot.interceptors;

/**
 * <p>
 * 字符串拼接处理器
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public class StringInterceptors implements Interceptors<String> {
    /**
     * 拦截器处理，用于为字符串拼接指定的前缀与后缀
     *
     * @param handler 字符串
     * @param prefix  前缀
     * @param suffix  后缀
     */
    @Override
    public String handle(String handler, String prefix, String suffix) {
        if (handler == null) {
            return null;
        }

        return prefix + handler + suffix;
    }


}
