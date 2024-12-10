package com.rabbit.foot.factory;

import com.rabbit.foot.Main;
import com.rabbit.foot.common.enums.ReptileType;
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
        ResolverFactory<BookData> resolverFactory = new ResolverFactory<>(Main.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.SEARCH, "剑来");
        List<BookData> list = resolverFactory.capture();

        logger.info("BookSearchTest.search size: " + list.size());
    }
}
