package com.alipay.config;

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
    public static String app_id = "2016110200787556";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCgQlKbVg4NFljOorfJFv6wMOX8eAEv8r0Y0uz5QtYYTT4vx40NFvOnF6AC1LQxYtEDf3ezJ5R0NKnyjgh85ImKqhkK4C88g/FGRn94CbGNn3U3t7tb58i4cLxkWru3kMuAjthD7WzXMSsBSXDvJmqu+I/FJM76ENUHCvJPalEjE6RA/RnaUY2cOi+LtmjatzxAwkU8lk6D9n0nhsCorCa4ughdSKeYSc9zlh6kknPYm6oiEYux2guK/AphYj3lz5T12liHal1xqipjrsl4wNNGegKkcyIfIMpWrd+DERdtdjJdqBEQTZk3FIoUapsqcuqESZazj09CkZ2TbjSAbs5zAgMBAAECggEADPrSLfqeULkKoFHViNfv8N1FA1nE3tlOL4CF6Ppc1MMM0lnbNsAxI8tqDlCj94wFWb8tNrlx8KeDTD3suhDYgv6b4KGfscsN858qG9LNKqdLmyOqbRssyqN23rnqkkeL8/Gd2oKUWimFTilgmD4ITfubyV/d06shVdyxG3WE9M41mbmi/IsHWdOvkmzNHneB9Z5ukt2zCEKmi71JObiJqSflWoKpQZDgJnK8buTF7uH0VcmefhMc7WziCzdqXoa/AritQ+lCq6rHnzbFzkmOb4zY3Vle1yFWoxVeUVTpehnYgUOFW0+YAjN2rOcXbCwXE5TvBjDAvX46u6cFA3GXgQKBgQDU056OP7ud6pmbcAZXsSBd+ap2vlAM/SCuETb1T87sRrpG0mF2I6MmLZh9KvluQ5m0E9uh7ItuLZ4dM0zOIArbeVTDGOGj+Id7PGpR1QZwLBjBDmYvRXfrgzU3dgFHw/Q1lFJcl6O+Kmh+sBHKhoi+cAeqXvCp0GNhX2CPZRG+EQKBgQDAxMwWZgPnW3Cq9qE8BnLFuRZyaNj5SA5kGKKCR5wNagOvraCY2Q7A14DeKkA9i14FHYy2Rt6UFCoOvh8y6QSy20JBrX70RJegM+Jqqb0++W0BC9COgnPtFN6kQWEWH8xT1a4gj5Mf1YX/ads19PaOGLGwYDSYBsag0J3qDVkQQwKBgQDSgt96ZJ/3e22kyhH7+z/AtItgGK6lqlsA0hZCX0aVfwARvVaZvHZL9R238SH3ZvZJoLrGV2sy9/xFddCqshcLL+AfeiT2Q4X+56tidguAOJ3jl5KLf3M2iPnCef6aNcEAnk05OOTF3LyvUwjGAnyPodBzPKVf0rf6QJUKQTZjgQKBgGbCgekOyNX0MMNyZdERvu0YFZc3vTJl7wX8++RnOBEOipiD/jB8xRiXyOKls0kbmisv7WrdrJlThngFcaw5+3880r0gN6UPS6TjA7fIdFG1tR3xpmPRtuqTTXxSPecFXG70YbaJ0uwNbzkQvQKuayCkmVRkdKT2N7xSeVvjca7xAoGBAJgX//RSfopP/YMCqHJbvrlIqXl089KUU3Lc4wdF47wj+CN/OX41MRRoXESIeBNHzN8UqB+0kNtXHGZC+x1WG/ridLTtycxbaPbA9TPN7Fe/ZHp87O/xxuBk/c69aDev59v0s+6dGwovMYRPVxyeU4shEzPs8F4ruqIAg/MbKHww";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAx7giDkpFG2z2icMY+59J97LcwvZsr4dHNDqAqFZXTSZDroH86R8AZ94immIAaluz0/8nZgBQmM4cekpEirLFHJbAQ9rO7nz/qgyFnMMqVT1F4Djp7FgMWzTqVOWe5AtMdSygkvM2L9mgqquhSi5+WCaWpHBu1EuQpiwyMB4syzUwTEb5djbbYVngZhJzL/qffIBLmXVu7cPkU5JwYGCos6x94qQMkY3O1oHpQOzUaf6I65UXKtfyxaLNla5Wpp1j+Lnf79ttcYPhIUUwZjG1uUj5f2GEx1Xws17ZPMGhlJA29tb8PsiHlS0gOqJDnXdrXdDMuhQlcx5lgGTG+8fwMQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8011/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8005/005-p2p-web/loan/page/alipayBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


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

