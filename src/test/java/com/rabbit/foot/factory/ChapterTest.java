package com.rabbit.foot.factory;

import com.rabbit.foot.enums.ReptileType;
import com.rabbit.foot.entity.Chapter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

/**
 * 章节爬虫测试类
 *
 * @author LiAo
 * @since 2024/12/10
 */
class ChapterTest {

    private static final Logger logger = Logger.getLogger(ChapterTest.class.getName());

    @Test
    void chapter() {
        ResolverFactory<Chapter> resolverFactory = new ResolverFactory<>(ChapterTest.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.CHAPTER, "http://localhost/efg/");
        List<Chapter> list = resolverFactory.capture();

        logger.info("ChapterTest.search size: " + list.size());

    }
}
