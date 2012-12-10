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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.PropConfigurations;

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
    
    private PropConfigurations  proxy;
  
    
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
    
    /**
     * 
     * 初始化 account 对象 
     *
     */
      protected  void init (){
        proxy = new PropConfigurations("proxy.properties");
        initProxy();
    }
    
    private void initProxy(){
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
         
        log.info("initConfigProxy over! size="+ HOST_MAP.size() );
    }
    
    public static void main (String [] args){
        ConfigHttpProxy.getInstance().getProxy();
        
       
       
//        log.info("initConfigProxy over! size="+ HOST_MAP.size() );
    }

 
    
}

