package com.rabbit.foot.constant;

/**
 * <p>
 * 爬虫行为Json Key 常量
 * </p>
 *
 * @author LiAo
 * @since 2024-04-08
 */
public class Constants {

    /**
     * 书籍爬虫行为集合
     */
    public static final String BOOKS = "books";

    /**
     * 动作名称，用作渲染页面数据源下拉框，用作分组
     */
    public static final String NAME = "name";

    /**
     * 资源类型 搜索、章节、内容 对应枚举类 ReptileType
     */
    public static final String SPIDER_TYPE = "spider-type";

    /**
     * 爬虫解析器类型
     */
    public static final String TYPE = "type";

    /**
     * 爬虫结果组装配置
     */
    public static final String CONFIG = "config";

    /**
     * 爬虫将数据组装的结果类型 类路径
     */
    public static final String JAVA_TYPE = "java-type";

    /**
     * 爬虫资源HTTP请求动作描述对象
     */
    public static final String SITE = "site";

    /**
     * 请求类型
     */
    public static final String METHOD = "method";

    /**
     * 请求路径
     */
    public static final String URL = "url";

    /**
     * 请求头
     */
    public static final String HEADERS = "headers";

    /**
     * 请求参数
     */
    public static final String BODY = "body";

    /**
     * 解析动作
     */
    public static final String RESOLVER_ACTION = "resolver-action";

    /**
     * 解析节点描述
     */
    public static final String ELEMENT = "element";

    /**
     * 节点查找属性类型
     */
    public static final String ELEMENT_TYPE = "element-type";

    /**
     * 属性值
     */
    public static final String ELEMENT_VALUE = "element-value";

    /**
     * 若有多个相同节点，获取从 0 开始指定下标的单个节点
     */
    public static final String LEAF_INDEX = "leaf-index";

    /**
     * 是否组黄结果
     */
    public static final String IS_LEAF = "is-leaf";

    /**
     * 如果获取节点的属性值，就填写对应的属性值名称，比如  src，href，
     * 若获取标签内容则填写 txt，若是获取html节点 则是 html
     */
    public static final String TARGET_KEY = "target-key";

    /**
     * 结果节点
     */
    public static final String RESULT_ELEMENT = "result-element";

    /**
     * 对应的属性值
     */
    public static final String RESULT_KEY = "result-key";

    /**
     * 结果拦截器
     */
    public static final String INTERCEPTORS = "interceptors";

    /**
     * 结果拦截器 前缀
     */
    public static final String PREFIX = "prefix";

    /**
     * 结果拦截器 后缀
     */
    public static final String SUFFIX = "suffix";

    /**
     * 要删除的节点集合
     */
    public static final String REMOVE = "remove";

    /**
     * 要删除的节点属性值类型
     */
    public static final String REMOVE_ELEMENT_TYPE = "element-type";

    /**
     * 要删除的节点属性值
     */
    public static final String REMOVE_ELEMENT_VALUE = "element-value";


}
