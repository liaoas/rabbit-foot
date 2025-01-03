package com.rabbit.foot.factory;

import com.rabbit.foot.enums.ReptileType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Logger;

/**
 * 章节爬虫测试类
 *
 * @author LiAo
 * @since 2024/12/10
 */
class ContentTest {

    private static final Logger logger = Logger.getLogger(ContentTest.class.getName());

    @Test
    void content() {
        ResolverFactory<String> resolverFactory = new ResolverFactory<>(ContentTest.class.getClassLoader().getResource("spider-action-dev.json"),
                "选书网", ReptileType.CONTENT, "http://localhost/hig/");
        List<String> list = resolverFactory.capture();

        logger.info("ContentTest.search size: " + list.size());
    }
}
