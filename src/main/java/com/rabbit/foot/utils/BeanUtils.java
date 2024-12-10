package com.rabbit.foot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbit.foot.constant.Constants;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public class BeanUtils {

    /**
     * 拦截器缓存
     */
    private static final Map<String, Object> BEANS = new HashMap<>();

    /**
     * 创建并获取拦截器
     *
     * @param jsonNode 对象类型
     */
    public static Object getBean(JsonNode jsonNode) {

        if (jsonNode == null || !jsonNode.has(Constants.TYPE)) {
            throw new RuntimeException("Bean对象获取失败，对象描述Json为空");
        }

        String javaType = jsonNode.get(Constants.TYPE).asText();

        return getBean(javaType);
    }

    /**
     * 创建并获取拦截器
     *
     * @param javaType 类型
     */
    public static Object getBean(String javaType) {

        if (BEANS.containsKey(javaType)) {
            return BEANS.get(javaType);
        }

        try {
            Object interceptors = Class.forName(javaType).getDeclaredConstructor().newInstance();
            BEANS.put(javaType, interceptors);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("名为 " + javaType + " 的对象创建失败");
        }

        if (!BEANS.containsKey(javaType)) {
            throw new RuntimeException("名为 " + javaType + " 的对象获取失败");
        }

        return BEANS.get(javaType);
    }
}
