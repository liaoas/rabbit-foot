package com.liao;

import com.liao.core.spider.html.HTMLSpiderResolver;

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

        HTMLSpiderResolver jd = new HTMLSpiderResolver();

        jd.execute("jd");
    }

}
