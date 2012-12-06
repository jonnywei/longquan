/**
 *@version:2012-12-6-上午11:32:26
 *@author:jianjunwei
 *@date:上午11:32:26
 *
 */
package com.sohu.wap.proxy;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.http.HttpUtil4Exposer;

/**
 * 
 * @author jianjunwei
 *
 */
public class ProxyHelper {

    private static Logger log = LoggerFactory.getLogger(HttpProxy.class);

    
    private static String TEST_PROXY_URL = "http://dev.w.sohu.com/t2/reqinfo.do";

    /**
     * 
     *测试代理是否可用 
     *返回正确的cookie和内容
     * 
     */
    public static boolean testProxy(String proxyIp, int port) {
      
        boolean  isCanUse = false;
        HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient(proxyIp, port);
        httpUtil4.addCookie("client_cookie", "client_cookie", ".sohu.com");
        long start = System.currentTimeMillis();
        String result = httpUtil4.getContent(TEST_PROXY_URL);
        long time = System.currentTimeMillis() - start;

        System.out.println(proxyIp + " result=" + result);
        System.out.println(proxyIp + " time=" + time);

        if (result == null) {
            System.out.println(proxyIp + "test error");
            log.error(proxyIp + "test error");
            isCanUse= false;
        } else {

            try {
                JSONObject rj = new JSONObject(result);
            } catch (Exception ex) {
                log.error("result error", ex);
                isCanUse= false;
                return isCanUse;
            }
            
            if (httpUtil4.getCookieValue("client_cookie").equals("client_cookie")
                    && httpUtil4.getCookieValue("cookie_test") != null
                    && httpUtil4.getCookieValue("cookie_test").equals("true")) {
                System.out.println(proxyIp + " check ok!");
                log.info(proxyIp + " check ok!");
                isCanUse= true;
            } else {
                System.out.println(proxyIp + "cookie  test error");
                log.error(proxyIp + "cookie  test error");
            }

        }
        return isCanUse;
    }
}
