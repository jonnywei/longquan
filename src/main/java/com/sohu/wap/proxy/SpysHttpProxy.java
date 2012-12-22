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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.sohu.wap.proxy.AbstractHttpProxy.ProxyChecker;
import com.sohu.wap.util.CheckProxyThreadPool;
import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.ScheduledThreadPool;
import com.sohu.wap.util.ThreadUtil;

/**
 * @author jianjunwei
 *
 */
public class SpysHttpProxy extends AbstractHttpProxy implements HttpProxy {
    
    
    private static Logger log = LoggerFactory.getLogger(SpysHttpProxy.class);
   
    static String SPYS_RU_PROXY_URL = "http://spys.ru/free-proxy-list/CN/";
    
    static String PROXY_NOVA_URL ="http://www.proxynova.com/proxy_list.txt?country=cn";

    static String prefix = "document.write(\"<font class=spy2>:<\\/font>\"+";

    static String TEST_URL = "http://w.sohu.com/t2/reqinfo.do";
    
    
    static long min_proxy_size  = 30;
    
  //单例对象
    private static  SpysHttpProxy  _instance;
    
    
    
    private static volatile boolean isInit = false;
   
    
    //初始化
   public static SpysHttpProxy getInstance(){
        
        if (_instance == null){
            synchronized (ConfigHttpProxy.class){
                if (_instance == null){
                    _instance  = new SpysHttpProxy();
                }
            }
        }
      return   _instance;
    }

    
    
    private  SpysHttpProxy (){
    	
    	try {
			loadHostProxyMap();
		} catch (ScriptException e) {
			log.error("load error");
		}
    	
        ProxyChecker proxyChecker =  new ProxyChecker();
//      proxyChecker.run();
        scheduledService.scheduleWithFixedDelay(proxyChecker,initialDelay, delay, TimeUnit.SECONDS);
        
        GetHostFromSpysTask loadTask = new GetHostFromSpysTask();
        
        scheduledService.scheduleWithFixedDelay(proxyChecker, 60*60, 60*60, TimeUnit.SECONDS);
        
        
    
    }
    
    protected class GetHostFromSpysTask implements Runnable {
        @Override
        public void run() {
            try {
                loadHostProxyMap();
            } catch (Exception ex) {
                log.error("throw exception", ex);
            }
            System.out.println("check after proxyhost size =" + HOST_MAP.size());
        }
    }; 
    
    
 
//    public   Map<String, Host> getHttpProxy() {
//
//        if (!isInit) {
//            synchronized (SpysHttpProxy.class) {
//
//                try {
//                    // 初始化hash map
//                    loadHostProxyMap();
//                } catch (ScriptException e) {
//                    log.error("script execute error", e);
//                }
//                TimerTask task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        try {
//                            Iterator iterator = SpysHttpProxy.getHttpProxy().keySet().iterator();
//                            while (iterator.hasNext()) {
//                                String key = (String) iterator.next();
//                                Host host = SpysHttpProxy.getHttpProxy().get(key);
//                                long begin = System.currentTimeMillis();
//                                boolean canUse = ProxyHelper.testProxy(host.getIp(), host.getPort());
//                                long time = (System.currentTimeMillis() - begin);
//                                System.out.println("request time=" + time);
//                                if (time > long_request_time){
//                                    if ( SpysHttpProxy.getHttpProxy().size() < min_proxy_size &&  time < max_request_time){
//                                        log.error("proxy list size too small. not remove .size =" +SpysHttpProxy.getHttpProxy().size());
//                                    }else{
//                                        log.error(key +" time="+time +" extend " + long_request_time);
//                                        System.out.println(key +" time="+time +" extend " + long_request_time);
//                                        iterator.remove();
//                                        continue;
//                                    }
//                                }
//                                if (! canUse) {
//                                    log.error(host + "can not Use,remove");
//                                    iterator.remove();
//                                } else {
//                                      log.info(host + " check ok!");
//                                }
//                                // 海驾的测试可用取消，速度太慢了
//                                // long hjbegin = System.currentTimeMillis();
//                                // String hjtest =
//                                // httpUtil4.getContent(YueChe.LOGIN_URL);
//                                // time = (System.currentTimeMillis() -
//                                // hjbegin);
//                                //                              
//                                // System.out.println("request haijia time" +
//                                // time);
//                                //                              
//                            }
//
//                        } catch (Exception ex) {
//                            log.error("throw exception", ex);
//                        }
//                        System.out.println("check after proxyhost size =" + SpysHttpProxy.getHttpProxy().size());
//                    }
//                };
//                
//               
//                timer.scheduleAtFixedRate(task, 0, 30 * 60 * 1000);
//                timer.scheduleAtFixedRate(loadTask, 60 * 60 * 1000, 60 * 60 * 1000);
//                isInit = true;
//            }
//        }
//
//        return HOST_MAP;
//
//    }

    private  void loadHostProxyMap(String url) throws ScriptException {

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

    
    
    private  void loadHostProxyMap() throws ScriptException {
            
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
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws ScriptException, InterruptedException {

        Map<String, Host>  hostMap =SpysHttpProxy.getInstance().getProxy();
		ThreadUtil.sleep(120);
		Iterator<String> iter = hostMap.keySet().iterator();
		int index =1000075;
		while(iter.hasNext()){
			index ++;
			String key = iter.next();
			Host host =hostMap.get(key);
			System.out.println(index+"="+host.getIp()+":"+host.getPort());
		}
		System.exit(0);
    }



    /* (non-Javadoc)
     * @see com.sohu.wap.proxy.AbstractHttpProxy#init()
     */
    @Override
    protected void init() {
        // TODO Auto-generated method stub
        
    }

}
