# ![](src/main/resources/logo.svg) json-spider 

这是一个使用 Json 来描述爬虫的动作，从而实现动态的爬虫。相较于通过硬编码来实现爬虫，这种方式更加灵活，只需要修改描述爬虫动作的Json就可以应对目标服务的改变，而无需从新编码

目前只支持使用 jsoup 对 HTML 页面的爬取
```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.16.1</version>
</dependency>

```