/**
 *@version:2012-12-5-上午10:53:13
 *@author:jianjunwei
 *@date:上午10:53:13
 *
 */
package com.sohu.wap.proxy;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.YueCheHelper;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.util.DateUtil;

/**
 * @author jianjunwei
 *
 */
public class HttpProxy {
    
    
    private static Logger log = LoggerFactory.getLogger(HttpProxy.class);
   

    private static Map<String, Host> HOST_MAP = new ConcurrentHashMap<String, Host>();

    static String SPYS_RU_PROXY_URL = "http://spys.ru/free-proxy-list/CN/";
    
    static String PROXY_NOVA_URL ="http://www.proxynova.com/proxy_list.txt?country=cn";

    static String prefix = "document.write(\"<font class=spy2>:<\\/font>\"+";

    static String TEST_URL = "http://dev.w.sohu.com/t2/reqinfo.do";
    
    static long long_request_time = 2000;
    
    static long max_request_time = 6000;
    
    static long min_proxy_size  = 30;
    

    private static volatile boolean isInit = false;
    private static Timer timer = new Timer();

    
    //初始化
    static{
        if (YueCheHelper.IS_USE_PROXY_BOOK_CAR){
            getHttpProxy();
        }
       
    }

 
    public static Map<String, Host> getHttpProxy() {

        if (!isInit) {
            synchronized (HttpProxy.class) {

                try {
                    // 初始化hash map
                    loadHostProxyMap();

                } catch (ScriptException e) {
                    log.error("script execute error", e);

                }

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Iterator iterator = HttpProxy.getHttpProxy().keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                Host host = HttpProxy.getHttpProxy().get(key);
                                HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(), host.getPort());
                                httpUtil4.addCookie("client_cookie", "client_cookie", ".sohu.com");
                                long begin = System.currentTimeMillis();
                                String result = httpUtil4.getContent(TEST_URL);
                                long time = (System.currentTimeMillis() - begin);
                                System.out.println("request time=" + time);
                                if (time > long_request_time){
                                    if ( HttpProxy.getHttpProxy().size() < min_proxy_size &&  time < max_request_time){
                                        log.error("proxy list size too small. not remove .size =" +HttpProxy.getHttpProxy().size());
                                    }else{
                                        log.error(key +" time="+time +" extend " + long_request_time);
                                        System.out.println(key +" time="+time +" extend " + long_request_time);
                                        iterator.remove();
                                        continue;
                                    }
                                  
                                }
                               
                                if (result == null) {
                                    System.out.println(host + "test error,remove");
                                    log.error(host + "test error,remove");
                                    iterator.remove();
                                } else {
                                    
                                    System.out.println(key + "=" + result);
                                    try{
                                        JSONObject rj = new JSONObject(result);
                                    } catch (Exception ex) {
                                        log.error("result error", ex);
                                        iterator.remove();
                                        continue;
                                    }
                                    
                                 
                                    if (httpUtil4.getCookieValue("client_cookie").equals("client_cookie")
                                            && httpUtil4.getCookieValue("cookie_test") != null
                                            && httpUtil4.getCookieValue("cookie_test").equals("true")) {
                                        System.out.println(host + "check ok!");
                                        log.info(host + " check ok!");
                                    } else {
                                        System.out.println(host + "cookie  test error,remove");
                                        log.error(host + "cookie  test error,remove");
                                        iterator.remove();
                                    }
                                }
                                // 海驾的测试可用取消，速度太慢了
                                // long hjbegin = System.currentTimeMillis();
                                // String hjtest =
                                // httpUtil4.getContent(YueChe.LOGIN_URL);
                                // time = (System.currentTimeMillis() -
                                // hjbegin);
                                //                              
                                // System.out.println("request haijia time" +
                                // time);
                                //                              
                            }

                        } catch (Exception ex) {
                            log.error("throw exception", ex);
                        }
                        System.out.println("check after proxyhost size =" + HttpProxy.getHttpProxy().size());
                    }
                };
                
                TimerTask loadTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            loadHostProxyMap();
                        } catch (Exception ex) {
                            log.error("throw exception", ex);
                        }
                        System.out.println("check after proxyhost size =" + HttpProxy.getHttpProxy().size());
                    }
                }; 
                
                timer.scheduleAtFixedRate(task, 0, 30 * 60 * 1000);
                timer.scheduleAtFixedRate(loadTask, 60 * 60 * 1000, 60 * 60 * 1000);
                isInit = true;
            }
        }

        return HOST_MAP;

    }

    private static void loadHostProxyMap(String url) throws ScriptException {

        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        
        HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient();

        String result = httpUtil4.getContent(url);
        if (result == null) {
            log.error("fetch proxy info error!");
            return;
        }
        // System.out.println(result);
        Document html = Jsoup.parse(result);
        String scriptStr = "body > script";
        String script = html.select(scriptStr).get(0).html();
        se.eval(script);

        String selectStr = "body > table";
        Elements elems = html.select(selectStr).get(1).select("tr").get(2).select("td > table > tr");
        int size = 34;
        for (int i = 3; i < (size - 1); i++) {
            Element elem = elems.get(i);
            Elements tds = elem.select("td");
            // System.out.println(tds.text());

            // 得到ip和port
            Element td0 = tds.get(0);
            Element fontNu = td0.select("font.spy1").get(0);
            // String id = fontNu.text();
            Element font = td0.select("font.spy14").get(0);
            String ip = font.text();
            String strPort = font.child(0).html().substring(prefix.length());
            strPort = "\"\"+" + strPort.substring(0, strPort.length() - 1);
            String port = (String) se.eval(strPort);

            Host host = new Host(ip, port);

            Element td1 = tds.get(1);
            host.setType(td1.text());
            Element td2 = tds.get(2);
            host.setAnonymity(td2.text());
            Element td3 = tds.get(3);
            host.setCity(td3.text());
            Element td4 = tds.get(4);
            host.setName(td4.text());
            Element td5 = tds.get(5);
            host.setCheckDate(DateUtil.getDate(td5.text(), "dd-MMM-yyyy HH:mm"));

            System.out.println(host);

            HOST_MAP.put(ip, host);

        }
    }

    
    
    private static void loadHostProxyMap() throws ScriptException {
            
        String url =  SPYS_RU_PROXY_URL;
        for(int i= 0; i< 4; i++){
            if (i != 0){
                 url =    SPYS_RU_PROXY_URL.replace("free-proxy-list", "free-proxy-list"+i);
            }
//            System.out.println(url);
            loadHostProxyMap(url);
        }
        System.out.println("load ok! size="+HOST_MAP.size());
 
    }

    /**
     * @param args
     * @throws ScriptException 
     */
    public static void main(String[] args) throws ScriptException {

        HttpProxy.getHttpProxy();

    }

}
