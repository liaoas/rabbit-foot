package com.rabbit.foot.factory;

import com.rabbit.foot.enums.ReptileType;
import com.rabbit.foot.entity.BookData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

/**
 * 书籍搜索测试类
 *
 * @author LiAo
 * @since 2024/12/10
 */
class BookSearchTest {

    private static final Logger logger = Logger.getLogger(BookSearchTest.class.getName());

    @Test
    void search() {
        ResolverFactory<BookData> resolverFactory = new ResolverFactory<>(BookSearchTest.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.SEARCH, "剑来");
        List<BookData> list = resolverFactory.capture();

        logger.info("BookSearchTest.search size: " + list.size());
    }

    @Test
    void jsonStrSearch() {
        String abc = "{\"books\": [{\"name\": \"选书网\",\"spider-type\": \"search\",\"type\": \"com.rabbit.foot.parser.html.HtmlParser\",\"config\": {\"java-type\": \"com.rabbit.foot.entity.BookData\"},\"site\": {\"method\": \"GET\",\"url\": \"http://localhost/abc/\",\"headers\": {\"Cookie\": \"cookie_value\"},\"params\": {\"searchkey\": \"{params[0]}\"}},\"resolver-action\": {\"element\": {\"element-type\": \"class\",\"element-value\": \"grid\",\"content-type\": \"array\",\"element\": {\"element-type\": \"tage\",\"element-value\": \"tr\",\"content-type\": \"array\",\"element\": {\"element-type\": \"result\",\"element-value\": \"item\",\"content-type\": \"array\",\"start-assembly\": true,\"remove\": [{\"element-type\": \"class\",\"element-value\": \"grid_1\"}],\"result-element\": [{\"element-type\": \"class\",\"element-value\": \"even\",\"content-type\": \"obj\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"text\",\"result-key\": \"bookName\",\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}},{\"element-type\": \"class\",\"element-value\": \"even\",\"content-type\": \"obj\",\"element\": {\"element-type\": \"tage\",\"element-value\": \"a\",\"content-type\": \"obj\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"href\",\"result-key\": \"bookLink\",\"result-end\": true,\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}}},{\"element-type\": \"class\",\"element-value\": \"even\",\"content-type\": \"obj\",\"leaf-index\": 1,\"is-leaf\": true,\"target-key\": \"text\",\"result-key\": \"author\",\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}},{\"element-type\": \"class\",\"element-value\": \"odd\",\"content-type\": \"obj\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"text\",\"result-key\": \"chapter\",\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}}]}}}}},{\"name\": \"选书网\",\"spider-type\": \"chapter\",\"type\": \"com.rabbit.foot.parser.html.HtmlParser\",\"config\": {\"java-type\": \"com.rabbit.foot.entity.Chapter\"},\"site\": {\"method\": \"GET\",\"url\": \"http://localhost/efg/\",\"headers\": {\"Cookie\": \"cookie_value\"},\"params\": {\"searchkey\": \"{params[0]}\"}},\"resolver-action\": {\"element\": {\"element-type\": \"class\",\"element-value\": \"pc_list\",\"content-type\": \"array\",\"element\": {\"element-type\": \"tage\",\"element-value\": \"li\",\"content-type\": \"array\",\"element\": {\"element-type\": \"result\",\"element-value\": \"item\",\"content-type\": \"array\",\"start-assembly\": true,\"result-element\": [{\"element-type\": \"tage\",\"element-value\": \"a\",\"content-type\": \"obj\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"text\",\"result-key\": \"name\",\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}},{\"element-type\": \"tage\",\"element-value\": \"a\",\"content-type\": \"obj\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"href\",\"result-key\": \"link\",\"result-end\": true,\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"http://www.booktxt.tw\",\"suffix\": \"\"}}]}}}}},{\"name\": \"选书网\",\"spider-type\": \"content\",\"type\": \"com.rabbit.foot.parser.html.HtmlParser\",\"config\": {\"java-type\": \"java.lang.String\"},\"site\": {\"method\": \"GET\",\"url\": \"http://localhost/hig/\",\"headers\": {\"Cookie\": \"cookie_value\"},\"params\": {\"searchkey\": \"{params[0]}\"}},\"resolver-action\": {\"element\": {\"element-type\": \"id\",\"element-value\": \"content1\",\"content-type\": \"obj\",\"element\": {\"element-type\": \"result\",\"element-value\": \"item\",\"content-type\": \"obj\",\"start-assembly\": true,\"result-element\": [{\"element-type\": \"content\",\"element-value\": \"content\",\"leaf-index\": 0,\"is-leaf\": true,\"target-key\": \"html\",\"result-key\": \"value\",\"interceptors\": {\"type\": \"com.rabbit.foot.interceptors.MyHandlerInterceptors\",\"prefix\": \"\",\"suffix\": \"\"}}]}}}}]}";

        ResolverFactory<BookData> resolverFactory = new ResolverFactory<>(abc, "选书网", ReptileType.SEARCH, "剑来");

        List<BookData> list = resolverFactory.capture();

        logger.info("BookSearchTest.jsonStrSearch size: " + list.size());
    }
}
