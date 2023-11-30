package com.rabbit.foot;

import com.rabbit.foot.core.factory.ResolverFactory;
import com.rabbit.foot.entity.ResultEntity;

import java.util.List;

/**
 *     {
 *       "site": {
 *         "method": "POST",
 *         "url": "http:localhost:80/job/post-test",
 *         "headers": {
 *           "Cookie": "application/json",
 *           "Authorization": "Bearer your_access_token_here"
 *         },
 *         "body": {
 *           "keyword": "三体"
 *         }
 *       }
 *     }
 * <p>
 * JsonSpiderBuild
 * </p>
 *
 * @author LiAo
 * @since 2023/8/2
 */
public class Main {
    public static void main(String[] args) {

        ResolverFactory<ResultEntity> jd = new ResolverFactory<>("jd");

        List<ResultEntity> jd1 = jd.execute("剑来");

        System.out.println();
    }

}
