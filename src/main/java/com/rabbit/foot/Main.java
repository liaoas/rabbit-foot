package com.rabbit.foot;

import com.rabbit.foot.common.enums.ReptileType;
import com.rabbit.foot.core.factory.ResolverFactory;
import com.rabbit.foot.core.github.GitHubFileReader;
import com.rabbit.foot.entity.BookData;
import com.rabbit.foot.entity.Chapter;

import java.util.List;

/**
 * Test
 *
 * @author LiAo
 * @since 2023/8/2
 */
public class Main {
    public static void main(String[] args) {

        // String abc = "{\"books\":[{\"name\":\"新笔趣阁\",\"spider-type\":\"search\",\"type\":\"com.rabbit.foot.core.spider.WebSpiderResolver\",\"site\":{\"method\":\"GET\",\"url\":\"http://localhost/abc/\",\"headers\":{\"Cookie\":\"此处填写京东登录Cookie\"},\"params\":{\"searchkey\":\"{params[0]}\"}},\"resolver-action\":{\"element\":{\"element-type\":\"class\",\"element-value\":\"grid\",\"content-type\":\"array\",\"element\":{\"element-type\":\"tage\",\"element-value\":\"tr\",\"content-type\":\"array\",\"remove\":[{\"element-type\":\"align\",\"element-value\":\"center\"}],\"element\":{\"element-type\":\"result\",\"element-value\":\"item\",\"content-type\":\"array\",\"start-assembly\":true,\"result-element\":[{\"element-type\":\"class\",\"element-value\":\"even\",\"content-type\":\"obj\",\"leaf-index\":0,\"is-leaf\":true,\"target-key\":\"text\",\"result-key\":\"title\",\"interceptors\":{\"type\":\"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\":\"\",\"suffix\":\"\"}},{\"element-type\":\"class\",\"element-value\":\"even\",\"content-type\":\"obj\",\"element\":{\"element-type\":\"tage\",\"element-value\":\"a\",\"content-type\":\"obj\",\"leaf-index\":0,\"is-leaf\":true,\"target-key\":\"href\",\"result-key\":\"url\",\"result-end\":true,\"interceptors\":{\"type\":\"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\":\"\",\"suffix\":\"\"}}},{\"element-type\":\"class\",\"element-value\":\"even\",\"content-type\":\"obj\",\"leaf-index\":1,\"is-leaf\":true,\"target-key\":\"text\",\"result-key\":\"author\",\"interceptors\":{\"type\":\"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\":\"\",\"suffix\":\"\"}},{\"element-type\":\"class\",\"element-value\":\"odd\",\"content-type\":\"obj\",\"leaf-index\":0,\"is-leaf\":true,\"target-key\":\"text\",\"result-key\":\"newChapter\",\"interceptors\":{\"type\":\"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\":\"\",\"suffix\":\"\"}}]}}}},\"config\":{\"java-type\":\"com.rabbit.foot.entity.ResultEntity\"}}]}";

        // ResolverFactory<ResultEntity> jd = new ResolverFactory<>(abc, "新笔趣阁", "search", "剑来");

        /*ResolverFactory<BookData> rf1 = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "书本网", "search", "剑来");
        ResolverFactory<Chapter> rf2 = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "书本网", "chapter", "http://www.booktxt.tw/list-13073/");*/
        ResolverFactory<String> rf3 = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.CONTENT, "http://localhost/hig/");
        ResolverFactory<BookData> rf1 = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "笔趣阁dev", ReptileType.SEARCH, "剑来");

        ResolverFactory<Chapter> rf2 = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.CHAPTER, "http://localhost/efg/");

        /*List<BookData> re1 = rf1.capture();*/
        List<Chapter> re2 = rf2.capture();
        /*List<String> re3 = rf3.capture();*/

        System.out.println();
    }

}
