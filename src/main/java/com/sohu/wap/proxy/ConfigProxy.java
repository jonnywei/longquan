/**
 *@version:2012-12-6-下午05:11:12
 *@author:jianjunwei
 *@date:下午05:11:12
 *
 */
package com.sohu.wap.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.AccountMap;
import com.sohu.wap.util.CheckProxyThreadPool;
import com.sohu.wap.util.PropConfigurations;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ScheduledThreadPool;

/**
 * 配置的代理
 * 
 * @author jianjunwei
 *
 */
public class ConfigProxy {
   
    private static Logger  log = LoggerFactory.getLogger(ConfigProxy.class);
    
    private static ConcurrentHashMap<String, Host> HOST_MAP = new ConcurrentHashMap<String, Host>();
    
    private static ExecutorService checkService =  CheckProxyThreadPool.getInstance().getExecutorService();
    
    private static ScheduledExecutorService scheduledService =   ScheduledThreadPool.getInstance().getScheduledExecutorService();
    
    
    //单例对象
    private static  ConfigProxy  _instance;
    
    private PropConfigurations  proxy;
    
    private static long initialDelay = 0;
    
    private static  long delay  =10*60;
    
    private  ConfigProxy (){
        init();
    }
 
    
    public static ConfigProxy getInstance(){
        
        if (_instance == null){
            synchronized (ConfigProxy.class){
                if (_instance == null){
                    _instance  = new ConfigProxy();
                    ProxyChecker proxyChecker = new ProxyChecker();
//                    proxyChecker.run();
                    scheduledService.scheduleWithFixedDelay(proxyChecker,initialDelay, delay, TimeUnit.SECONDS);
                    
                 }
            }
        }
      return   _instance;
    }
    
    
    public static class ProxyChecker implements Runnable {
        @Override
        public void run() {
            List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>(); 
            System.out.println("check proxy task");
            Object []  proxyArray =  HOST_MAP.keySet().toArray();
            System.out.println("check");
            int index =0;
            for (index =0; index < proxyArray.length; index++ ){
                 final String key  = (String )proxyArray[index];
                 final Host host  = HOST_MAP.get(key);
                resultList.add( checkService.submit(new Callable<Boolean>() {
                     @Override
                    public Boolean call() throws Exception {
                          return Boolean.valueOf(ProxyHelper.testProxy( host )) ;
                   }
                }));
            }
            for (index =0; index < resultList.size(); index++ ){
            	 final String key  = (String )proxyArray[index];
                Future<Boolean> fs  =   resultList.get(index);
                try {  
                    Boolean checkOk = fs.get(2, TimeUnit.SECONDS);
                    if (!checkOk){
                        HOST_MAP.remove(key);
                    }
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                } catch (ExecutionException e) {  
                
                }  catch (TimeoutException e) {
                    //超时删除
                	log.error(key+" timeout! remove");
                    HOST_MAP.remove(key);
                }
            }  
            log.info("schedule check over! size="+ HOST_MAP.size() );
            
            
            
        }}
    public  ConcurrentHashMap  <String, Host>   getProxy(){
    	 
              return HOST_MAP;
      }
    
    
    public  Host  getRandomHost(){
    	 Object []  proxyArray =  HOST_MAP.keySet().toArray();
    	 return  HOST_MAP.get( proxyArray[RandomUtil.getRandomInt(proxyArray.length)]);
   }
    
    /**
     * 
     * 初始化 account 对象 
     *
     */
    private  void init (){
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
        ConfigProxy.getInstance().getProxy();
        while(true){
        	System.out.println( ConfigProxy.getInstance().getRandomHost());
        }
       
//        log.info("initConfigProxy over! size="+ HOST_MAP.size() );
    }

 
    
}

