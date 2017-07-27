## 青柳云 API网关调用SDK


## 简介

本项目是青柳云API网关产品中，访问API使用的SDK项目。

## 使用

1. 引入依赖包

```xml
    <dependency>
        <groupId>cn.dceast.platform</groupId>
        <artifactId>gateway-sign</artifactId>
        <version>2.1.0</version>
    </dependency>
```
  
2. 调用api

```java    
    //api地址
    String url = "http://localhost:8080/route/get";
    //青柳云申请的appkey与secretkey
    String appKey = "test-appkey";
    String secretKey = "test-secretkey";
    
    //连接超时时间
    int timeout = 2000;

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("city", "苏州");
    params.put("bus", "301");
    String response = SimpleApiCallUtil.postForm(url, appKey, secretKey, params, null, timeout, null);
    System.out.println(response);
```

## 常见问题