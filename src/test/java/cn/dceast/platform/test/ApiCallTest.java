package cn.dceast.platform.test;

import java.util.HashMap;
import java.util.Map;

import cn.dceast.platform.gateway.sign.SimpleApiCallUtil;


public class ApiCallTest {

    public static void testGet() {
        String url = "http://localhost:8080/station/get?city=苏州&station=爱河桥";
        String appKey = "test-appkey";
        String secretKey = "test-secretkey";
        int timeout = 2000;
        String response = SimpleApiCallUtil.get(url, appKey, secretKey, null, timeout, null);
        System.out.println(response);
    }

    public static void testPostForm() {
        String url = "http://localhost:8080/route/get";
        String appKey = "test-appkey";
        String secretKey = "test-secretkey";
        int timeout = 2000;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("city", "苏州");
        params.put("bus", "301");
        String response = SimpleApiCallUtil.postForm(url, appKey, secretKey, params, null, timeout, null);
        System.out.println(response);
    }

    public static void testPostJson() {
        String url = "http://localhost:8080/faceback";
        String appKey = "test-appkey";
        String secretKey = "test-secretkey";
        int timeout = 2000;

        String params = "{\"name\":\"张三\",\"sex\":\"男\",\"mobile\":\"13411111111\",\"problem\":\"301路公交班次太少，希望解决。\"}";
        String response = SimpleApiCallUtil.postJson(url, appKey, secretKey, params, null, timeout, null);
        System.out.println(response);
    }

    public static void main(String[] args) {
        //testGet();

        //testPostForm();

        testPostJson();
    }
}
