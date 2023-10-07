package com.liao;

import com.liao.core.factory.ResolverFactory;
import com.liao.entity.ResultEntity;

import java.util.List;

/**
 *
 *
 * //    {
 * //      "site": {
 * //        "method": "POST",
 * //        "url": "http://localhost:80/job/post-test",
 * //        "headers": {
 * //          "Cookie": "application/json",
 * //          "Authorization": "Bearer your_access_token_here"
 * //        },
 * //        "body": {
 * //          "keyword": "三体"
 * //        }
 * //      }
 * //    }
 * <p>
 * JsonSpiderBuild
 * </p>
 *
 * @author LiAo
 * @since 2023/8/2
 */
public class Main {
    public static void main(String[] args) {

        ResolverFactory<ResultEntity> jd = new ResolverFactory<>();

        List<ResultEntity> jd1 = jd.execute("jd");

        System.out.println();
    }

}
