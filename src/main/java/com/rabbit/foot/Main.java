package com.rabbit.foot;

import com.rabbit.foot.core.factory.ResolverFactory;
import com.rabbit.foot.entity.ResultEntity;

import java.util.List;

/**
 * {
 * "site": {
 * "method": "POST",
 * "url": "http:localhost:80/job/post-test",
 * "headers": {
 * "Cookie": "application/json",
 * "Authorization": "Bearer your_access_token_here"
 * },
 * "body": {
 * "keyword": "三体"
 * },
 * "params": {
 * "q": "{params[0]}"
 * }
 * }
 * <p>
 * JsonSpiderBuild
 * </p>
 *
 * @author LiAo
 * @since 2023/8/2
 */
public class Main {
    public static void main(String[] args) {

        ResolverFactory<ResultEntity> jd = new ResolverFactory<>("新笔趣阁", "search", "剑来");

        // ResolverFactory<ResultEntity> jd = new ResolverFactory<>(ActionResources.class.getClassLoader().getResource("spider-action-test.json"), "壁纸网", "search", "剑来");

        List<ResultEntity> jd1 = jd.capture();

        System.out.println();
    }

}
