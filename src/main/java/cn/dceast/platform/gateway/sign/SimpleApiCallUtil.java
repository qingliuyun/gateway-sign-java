package cn.dceast.platform.gateway.sign;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.dceast.platform.gateway.sign.http.SimpleHttpSender;
import cn.dceast.platform.gateway.sign.http.SimpleHttpSender.HttpMethod;
import cn.dceast.platform.gateway.sign.util.StringUtil;

/**
 * API网关接口调用工具<br/>
 * <p>
 * Created by hongkai on 2016/1/21.
 */
public class SimpleApiCallUtil {
    private static Logger logger = LoggerFactory.getLogger(SimpleApiCallUtil.class);

    private static final int default_timeout = 2000;

    /**
     * 发送get请求，调用api
     *
     * @param url       请求地址
     * @param appKey
     * @param secretKey
     * @param timeout
     * @param headers   自定义请求头。一般不用填写
     * @return
     */
    public static byte[] get(String url, String appKey, String secretKey, int timeout, Map<String, Object> headers) {
        logger.info(String.format("Invoke api %s", url));

        if (timeout <= 0) {
            timeout = default_timeout;
        }

        Map<String, Object> signHeaders = SignatureHelper.buildSignHeader(appKey, secretKey);
        if (headers != null && headers.size() > 0) {
            signHeaders.putAll(headers);
        }

        byte[] response = SimpleHttpSender.request(url, HttpMethod.GET, null, signHeaders, timeout);

        return response;
    }

    /**
     * 发送get请求，调用api
     *
     * @param url       请求地址
     * @param appKey
     * @param secretKey
     * @param encoding  编码方式，默认utf-8
     * @param timeout
     * @param headers   自定义请求头。一般不用填写
     * @return
     */
    public static String get(String url, String appKey, String secretKey, String encoding,
                             int timeout, Map<String, Object> headers) {
        try {
            if (StringUtil.isEmpty(encoding)) {
                encoding = SimpleHttpSender.default_encoding;
            }

            byte[] b = get(url, appKey, secretKey, timeout, headers);
            return new String(b, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 发送post请求调用API接口
     *
     * @param url       请求地址（必填）
     * @param appKey    （必填）
     * @param secretKey （必填）
     * @param params    请求参数
     * @param encoding  编码方式。默认utf-8
     * @param timeout   请求超时时间
     * @param headers
     * @return
     * @throws Exception
     */
    public static byte[] post(String url, String appKey, String secretKey, byte[] params,
                              String encoding, int timeout, Map<String, Object> headers) throws Exception {

        logger.info(String.format("Invoke api %s", url));

        if (StringUtil.isEmpty(encoding)) {
            encoding = SimpleHttpSender.default_encoding;
        }

        if (timeout <= 0) {
            timeout = default_timeout;
        }

        Map<String, Object> signHeaders = SignatureHelper.buildSignHeader(appKey, secretKey);
        if (headers != null && headers.size() > 0) {
            signHeaders.putAll(headers);
        }

        byte[] response = SimpleHttpSender.request(url, HttpMethod.POST, params, signHeaders, timeout);

        return response;
    }

    /**
     * 发送post请求调用API接口。发送form表单格式数据
     *
     * @param url       请求地址（必填）
     * @param appKey    （必填）
     * @param secretKey （必填）
     * @param params    请求参数
     * @param encoding  编码方式。默认utf-8
     * @param timeout   请求超时时间
     * @param headers
     * @return
     * @throws Exception
     */
    public static String postForm(String url, String appKey, String secretKey, Map<String, Object> params,
                                  String encoding, int timeout, Map<String, Object> headers) {

        Map<String, Object> dataHeader = new HashMap<String, Object>();
        dataHeader.put("Content-Type", SimpleHttpSender.ContentType.application_x_www_form_urlencoded);
        copyHeader(headers, dataHeader);

        //设置默认编码
        if (StringUtil.isEmpty(encoding)) {
            encoding = SimpleHttpSender.default_encoding;
        }

        try {
            String paramsResult = toFromParam(params);
            byte[] bResult = null;
            if (StringUtil.isNotEmpty(paramsResult)) {
                bResult = paramsResult.getBytes(encoding);
            }

            byte[] b = post(url, appKey, secretKey, bResult, encoding, timeout, dataHeader);

            return new String(b, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    /**
     * 发送post请求调用API接口。发送Json表单格式数据
     *
     * @param url       请求地址（必填）
     * @param appKey    （必填）
     * @param secretKey （必填）
     * @param params    请求参数。json格式文本
     * @param encoding  编码方式。默认utf-8
     * @param timeout   请求超时时间
     * @param headers
     * @return
     * @throws Exception
     */
    public static String postJson(String url, String appKey, String secretKey, String params, String encoding,
                                  int timeout, Map<String, Object> headers) {

        Map<String, Object> dataHeader = new HashMap<String, Object>();
        dataHeader.put("Content-Type", SimpleHttpSender.ContentType.application_json);
        copyHeader(headers, dataHeader);

        //设置默认编码
        if (StringUtil.isEmpty(encoding)) {
            encoding = SimpleHttpSender.default_encoding;
        }

        try {
            byte[] bResult = null;
            if (StringUtil.isNotEmpty(params)) {
                bResult = params.getBytes(encoding);
            }

            byte[] b = post(url, appKey, secretKey, bResult, encoding, timeout, dataHeader);

            return new String(b, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    private static Map<String, Object> copyHeader(Map<String, Object> src, Map<String, Object> dest) {
        if (isEmptyOfMap(src)) {
            return dest;
        }

        if (isEmptyOfMap(dest)) {
            return src;
        }

        dest.putAll(src);

        return dest;

    }

    private static boolean isEmptyOfMap(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return true;
        }

        return false;
    }

    /**
     * 转换成form表单数据格式
     *
     * @param params
     * @return
     */
    private static String toFromParam(Map<String, Object> params) {
        if (isEmptyOfMap(params)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue().toString(), SimpleHttpSender.default_encoding))
                        .append("&");
            }

            String result = sb.toString();
            if (StringUtil.isEmpty(result)) {
                return null;
            }

            return result.substring(0, result.length() - 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
