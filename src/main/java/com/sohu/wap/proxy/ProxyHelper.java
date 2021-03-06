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

import com.sohu.wap.bo.Result;
import com.sohu.wap.http.HttpUtil4Exposer;

/**
 * 
 * @author jianjunwei
 *
 */
public class ProxyHelper {

    private static Logger log = LoggerFactory.getLogger(ProxyHelper.class);

    
    private static String TEST_PROXY_URL = "http://w.sohu.com/t2/reqinfo.do";
    
    
    /**
     * 
     *测试代理是否可用 
     *返回正确的cookie和内容
     * 
     */
    public static Result<Long> testProxy2(Host host) {
        
       Result<Long> result = new Result<Long>(Result.FAILURE);
       long start = System.currentTimeMillis();
       boolean checkResult  =     testProxy(host.getIp(), host.getPort());
       
       if(checkResult){
           result.setRet(Result.SUCCSS);
       }
       long time = System.currentTimeMillis() - start;
//       log.debug(host.getIp() + " time=" + time);
       result.setData(time);
       return result;
    }
    
    
    
    
    /**
     * 
     *测试代理是否可用 
     *返回正确的cookie和内容
     * 
     */
    public static boolean testProxy(Host host) {
    	
       long start = System.currentTimeMillis();
       boolean result = 	testProxy(host.getIp(), host.getPort());
       long time = System.currentTimeMillis() - start;
//       log.debug(host.getIp() + " time=" + time);
       host.setSpeed(time);
       return result;
    }

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
        JSONObject rj = new JSONObject();
        JSONObject result = httpUtil4.postJson( TEST_PROXY_URL ,rj);
        log.debug(proxyIp + " result=" + result);
        if (result == null) {
             log.error(proxyIp + " test error");
            isCanUse= false;
        } else {
//
//            try {
//                JSONObject rj = new JSONObject(result);
//            } catch (Exception ex) {
//                log.error("result error", ex);
//                isCanUse= false;
//                return isCanUse;
//            }
            
            if (httpUtil4.getCookieValue("client_cookie").equals("client_cookie")
                    && httpUtil4.getCookieValue("cookie_test") != null
                    && httpUtil4.getCookieValue("cookie_test").equals("true")) {
                
//                log.info(proxyIp + " check ok!");
                isCanUse= true;
            } else {
                log.error(proxyIp + "cookie  test error");
            }

        }
        return isCanUse;
    }
}
