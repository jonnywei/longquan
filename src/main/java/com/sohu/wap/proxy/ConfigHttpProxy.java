/**
 *@version:2012-12-6-下午05:11:12
 *@author:jianjunwei
 *@date:下午05:11:12
 *
 */
package com.sohu.wap.proxy;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.util.PropConfigurations;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadUtil;

/**
 * 配置的代理
 * 
 * @author jianjunwei
 *
 */
public class ConfigHttpProxy extends AbstractHttpProxy implements HttpProxy {
   
    private static Logger  log = LoggerFactory.getLogger(ConfigHttpProxy.class);
   
    //单例对象
    private static  ConfigHttpProxy  _instance;
   
    private  ConfigHttpProxy (){
        init();
        ProxyChecker proxyChecker =  new ProxyChecker();
//      proxyChecker.run();
        scheduledService.scheduleWithFixedDelay(proxyChecker,initialDelay, delay, TimeUnit.SECONDS);
    
    }
 
    
    public static ConfigHttpProxy getInstance(){
        
        if (_instance == null){
            synchronized (ConfigHttpProxy.class){
                if (_instance == null){
                    _instance  = new ConfigHttpProxy();
                }
            }
        }
      return   _instance;
    }
    
    public static void start(){
    	 ConfigHttpProxy.getInstance();
    }
    /**
     * 
     * 初始化 account 对象 
     *
     */
      protected  void init (){
    	loadProxyFromLocal();
        boolean useNetAdmin =  SystemConfigurations.getSystemBooleanProperty(Constants.CFG_SYSTEM_USE_NET_ADMIN, false);
        if(useNetAdmin){
        	loadProxyFromNet(); 
		 }   
        
    }
    
    private void loadProxyFromLocal(){
    	PropConfigurations proxy = new PropConfigurations("proxy.properties");
        Properties mapdb =  proxy.getProperties();
        Iterator itor =mapdb.keySet().iterator();
        while(itor.hasNext())
        {
           String key = ((String)itor.next()).trim() ;
      
           String value =(String) mapdb.get(key);
        
           String temp [] =  value.split("[:;]");
           
           int port =80;
           String ip = temp[0];
           if (temp.length > 1){
               port = Integer.valueOf(temp[1].trim());
           }
           
           Host host = new Host(ip, port);
           HOST_MAP.putIfAbsent(ip, host);
         }
         
        log.info("loadProxyFromLocal over! size="+ HOST_MAP.size() );
    }
    
    
     private void loadProxyFromNet(){
         try {
             JSONArray ycArray = new JSONArray();;
             String  ycInfo  = HttpUtil4Exposer.getInstance().getContent(Constants.PROXY_URL);
             if(ycInfo != null){
                    ycArray  = new JSONArray(ycInfo);
                
             }else{
                 log.error("get Proxy info from server error");
                 return;
             }
             for (int index =0; index < ycArray.length(); index ++){
                
                JSONObject yc =  ycArray.getJSONObject(index);
                
                Host host = Host.jsonToHost(yc);
                if (host.getAliveRate() > 70){
                    HOST_MAP.putIfAbsent(host.getIp(), host);
                }
             }
            } catch (JSONException e) {
                log.error("loadProxyFromNet from net error", e);
            }
             log.info("loadProxyFromNet over! size=" +  HOST_MAP.size() );
     }
    
    public static void main (String [] args) throws InterruptedException{
        ConfigHttpProxy.getInstance().getProxy();
        
        ThreadUtil.sleep(120);
		Iterator<String> iter = ConfigHttpProxy.getInstance().HOST_MAP.keySet().iterator();
		int index =1000075;
		while(iter.hasNext()){
			index ++;
			String key = iter.next();
			Host host =ConfigHttpProxy.getInstance().HOST_MAP.get(key);
			System.out.println(index+"="+host.getIp()+":"+host.getPort());
		}
		System.exit(0);
       
//        log.info("initConfigProxy over! size="+ HOST_MAP.size() );
    }

 
    
}

