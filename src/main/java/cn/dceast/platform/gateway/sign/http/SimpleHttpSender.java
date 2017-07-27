package cn.dceast.platform.gateway.sign.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.omg.CORBA.PUBLIC_MEMBER;


/**
 * http 发送工具包
 *
 * @author zhang
 */
public class SimpleHttpSender {

    public enum HttpMethod {
        POST, GET
    }

    public class ContentType {
        public static final String application_x_www_form_urlencoded = "application/x-www-form-urlencoded";
        public static final String application_json = "application/json";
        public static final String application_xml = "application/xml ";
        public static final String text_plain = "text/plain";
        public static final String application_octet_stream = "application/octet-stream";

    }

    public static final String default_encoding = "utf-8";
    
    //默认读取报文超时时间
    public static final int DEFAULT_READTIMEOUT=10000;
    /**
     * http请求
     *
     * @param url      请求地址
     * @param method   请求方法
     * @param params   参数
     * @param headers  请求头
     * @param encoding 编码
     * @param timeout  链接超时时间（毫秒）
     * @return
     * @throws IOException
     */
    public static String request(String url,
                                 HttpMethod method,
                                 String params,
                                 Map<String, Object> headers,
                                 String encoding,
                                 int timeout) {
        try {
            byte[] paramTemp = params == null ? null : params.getBytes(encoding);
            byte[] b = request(url, method, paramTemp, headers, timeout);

            return new String(b, encoding);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Net exception:" + e.getMessage());
        }
    }


    /**
     * http请求
     *
     * @param url      请求地址
     * @param method   请求方法
     * @param params   参数
     * @param headers  请求头
     * @param encoding 编码
     * @param timeout  链接超时时间（毫秒）
     * @return
     * @throws IOException
     */
    public static byte[] request(String url,
                                 HttpMethod method,
                                 byte[] params,
                                 Map<String, Object> headers,
                                 int timeout) {

        OutputStream out = null;
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(DEFAULT_READTIMEOUT);

            if (headers != null && headers.size() > 0) {
                Iterator<Entry<String, Object>> iter = headers.entrySet().iterator();

                while (iter.hasNext()) {
                    Entry<String, Object> entry = iter.next();
                    connection.setRequestProperty(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
                }
            }


            out = connection.getOutputStream();

            if (params != null && params.length > 0) {
                out.write(params);
            }

            out.flush();

            int code = connection.getResponseCode();

            if (code >= 400) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }

            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i;
            while ((i = in.read(b)) != -1) bos.write(b, 0, i);

            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Net exception:" + e.getMessage());
        } finally {
            closeOutputStream(out);
            closeOutputStream(bos);
            closeInputStream(in);
        }
    }

    public static void closeReader(Reader in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeInputStream(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
