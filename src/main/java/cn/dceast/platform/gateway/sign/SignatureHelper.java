package cn.dceast.platform.gateway.sign;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成签名工具
 * 用户调用平台API时，需要在Http Header中包含以下参数
 * user-date： API调用日期。格式yyyyMMddHHmmssSSS。17位日期字符串，小时采用24小时格式，如：20160101160758675。
 * user-params：采用base64编码后的用户参数。如果有多个参数，则以英文的&分隔。例如Base64("param1=t1&param2=t2")。
 * dceast-appkey：用户申请的appkey。
 * authorization： 鉴权码。
 * 其中，鉴权码（authorition）=dc:{appkey}:{signature}。鉴权码由三部分组成，他们之间以英文冒号隔开。
 * 第一部分：固定值为dc。
 * 第二部分：用户申请的appkey。
 * 第三部分：数据签名（signature）。
 * 例如：dc:test-appkey:ODExQUJENUFCRTBDQTc5NzM4RkQ2RTUzQzE2MDQyNUY=。
 * <p>
 * 数据签名（signature），加密格式如下：
 * signature=
 * BASE64{
 * Hmac_sha1{
 * user-params + 换行符
 * + user-date + 换行符
 * }
 * }
 * 数据签名由hmac_sha1加密后在经base64编码。hmac_sha1密钥为用户申请的SecretKey。
 *
 * @author zhang
 */
public class SignatureHelper {

    private static Logger logger = LoggerFactory.getLogger(SignatureHelper.class);

    public static final String SIGN_PREFIX = "dc";
    public static final String MSG_SPLITOR = "\n";

    /**
     * 生成API请求需要的Http Header。请情请参考类说明。
     * 用户参数。如果有多个参数，则以英文的&分隔。
     * @param appkey    公钥
     * @param secretkey 私钥
     * @return
     */
    public static Map<String, Object> buildSignHeader(String appkey, String secretkey) {
        Map<String, Object> header = new HashMap<>();
        //获取一个指定格式的api调用日期
        String signDate = getSignDate();
        //采用base64编码后的用户参数
        String userParams = Base64.encode(signDate);
        logger.debug(String.format("user-date: %s", signDate));
        logger.debug(String.format("user-params: %s", userParams));

        header.put("user-date", signDate);
        header.put("user-params", userParams);
        //拼接userParams和signDate
        String signData = buildSignData(userParams, signDate);
        //数据签名（signature）
        String sign = buildSignature(appkey, secretkey, signData);

        logger.debug(String.format("authorization: %s", sign));
        logger.debug(String.format("dceast-appkey: %s", appkey));

        header.put("authorization", sign);
        header.put("dceast-appkey", appkey);

        return header;
    }

    /**
     * 获取API调用日期。格式yyyyMMddHHmmssSSS。17位日期字符串，小时采用24小时格式，如：20160101160758675。
     *
     * @return
     */
    private static String getSignDate() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }

    /**
     * 生成需要签名的数据，格式为"user-params + 换行符  + user-date + 换行符"
     *
     * @param userParams 用户参数。如果有多个参数，则以英文的&分隔。
     * @param date       API调用日期。日期格式：yyyyMMddHHmissSSS
     * @return
     */
    private static String buildSignData(String userParams, String date) {
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(userParams).append(MSG_SPLITOR).append(date).append(MSG_SPLITOR);

        return sbBuffer.toString();

    }

    /**
     * 生成电子签名
     *
     * @param appkey    公钥
     * @param secretkey 私钥
     * @param data      签名数据。由buildSignData方法生成。
     * @return 签名字符串
     */
    private static String buildSignature(String appkey, String secretkey, String data) {

        String encodeData = Base64.encode(HMACSHA1.encode(data, secretkey));
        String sign = String.format("%s:%s:%s", SIGN_PREFIX, appkey, encodeData);

        return sign;
    }

    public static void main(String[] args) {
        String appkey = "E36B47759BC3C308";
        String secretkey = "qlr2dj6QorxXtHLb4lY8xq0C7KYdMaDE";
        String userParams = Base64.encode("test");
        String date = getSignDate();

        System.out.println("user-date:" + date);
        System.out.println("user-params:" + userParams);
        System.out.println("dceast-appkey:" + appkey);
        String signData = buildSignData(userParams, date);
        String authorization = buildSignature(appkey, secretkey, signData);
        System.out.println("authorization:" + authorization);
    }

}
