package util;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016092500597072";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCg/Pa9Ug7XxkQs2tfLDT9yxg32p8pbJfe3jWID1o5p4egQRjc/WNILtt4M9gD3ruox1bmI0QMnGCj0h78val0dxX/fnnLvYPzgg2ZMpIlrl9qfg3lbPSfbbZZYeNlX17XFyTFOul0JHBg7R4eBg4+J56zkIfcLT8V0SgbEQ4AY+H+wbi3e08gpjWtjWLyFipT0SoShByBm/JAcQbZGgkTy5DSpXDph5sIyD5t/Dfx/TxJ7/mmDLnO6eBNxjmO+XTYgH4YYkryeE49FN79vPppJjbT4C4HyU18p8U0ftQ4Fg/aN8z2szQPFsl5szdGxJlF3HO0xTUu9jbpLvbMVgXJbAgMBAAECggEAOW1DTxGAffQZya5fH+2f3n9L59q+0njevrlOZ3QX5SlSsMt7VbuHYepfl1wByFi67gABaQ62AzIZGfF6f51/jekvzkMlWMLOMgBtc5WV9615rUEm/yb3msyivviM/0rRarZ0QlM0cNV2x16itc6KW3UrNU3wZDaWhv8JHuW9Qip/5UXpP/oAiDggU/413a9bV6jidMJliP2sFf/E6go3/HlGEvAR5VB1SDN5UtR8yMcjeyrifN+wU34i1aMs6d2ok8Qbh0JsLrS76EM8qxBij0V2hNAfMWktj0A3jM5Iks1nNfrkMOVQcduenoI/C9jdUtIyw889VouoVPdU34I9wQKBgQDwjOburyoOkTwd/HkszRyoQ20o7CgRGJxxsUEwmkv0gR2TZngHhARBFamIUT23XZkTwcHdnRYdyak+aKqf/Bx9ED1lGz5Y7ZRz1U8WDXQtiKZTcKhPU6NeYYI2gRZrPue4Gzvprqq6puSQMYVTVDXf8BzlMofoK1PaQJIcOsuPMQKBgQCrU+jagkJ03hCZ21CMw9OOOJUP3hRdCg2gzD5neTFG2CCG+lUAU30phZo/hmenTLmmT2C7CEIyeFkGAfgJ9jesRzgvc3xXw1I/UmVPwpTQwceVRxnqyjj1+yiwoz4ac9jSX67fWcXZWNc3v7VpbLsUeUYCXdfLiYra7xM3HbyvSwKBgEZGQ6jzT2wWDeQMwDHQclDN8fQEmef0sq+cQxk52nrBxawsQWCgtsV02KRUZ51MIM5eyo49uBpXsbPjzAUVGBWJSwZgoSRFtBrq9EHTCF/NDhpOTT1vh8io3+ugZrZKGFP20NgCCSqiWx+s9/BSJ7MWaLqkuoOfNM4MEyNgIdvhAoGADA8y87Z5gQpkdr/2MoJSMM+QxnYmTso3XVsjBVuFeWwbFgUm2IgH5lVOHyyRvTfsVWV+9ItHRZAtC/w1flYDYy/8J3LkjLTl5CkjknKq0Ori5oIDtAAZ9E6CdbR32ZrP5zbu8y+5emwDchwfY9VQccd7gw0h9GSxDmoHmziPoHMCgYB42cBGcWVMhaWOve5RvEA9wZQxMv/tNujm6Mpd57OQI++jcq/PICxw3gCYvqPHwGoyJgarFgTrWgZ97j7kwnz7BRHweNNyPnCmfv0u7oQsqwwfgzW8CHnA1Na3g1yW5VhwCsDEMTDAjDMXByIEn9Nlz12HeIaZR3KCZJ9FYHWIJg==";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxkArn8zDA" +
            "diHTE2ehEJSAoThJh/Xg3hDH7ImABFfrwb1oTuzoK0pwGeMO/VYKcdEWSeAsnPxU0fpL3FwUJjcyjDjCa/a1Oaj" +
            "9YRjBwBt1fMbsKLRgI0DUdVTaLBryO/VEvHgxVBxBCOGhbHx+hi0pp+AFufE/w9IBz+JR0MNa7f8GtFoVEmciHyM" +
            "Fi3UKi+AoMexzUS8ykqMXGmzbc8jR3tBCh6UG8CShycPMzGdY4B7NIuAIqFCW+zLOvo7H3XdvTqMqMjxwa8bvzY8y5" +
            "3SuyeCve0jNYo0tqCmSkIP2sZB7OXnmizKEhNSKjNmM7jmBnCSrVne2V2W/PpK4L9vjQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://localhost:9103/paysuccess.html#";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "E:\\secret_key_tool\\log";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

