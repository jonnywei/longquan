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
public class ConfigProxy2 {
   
    private static Logger  log = LoggerFactory.getLogger(ConfigProxy2.class);
    
    
    private static ConcurrentHashMap<String, Host> HOST_MAP = new ConcurrentHashMap<String, Host>();
    
    private static ExecutorService checkService =  CheckProxyThreadPool.getInstance().getExecutorService();
    
    private static ScheduledExecutorService scheduledService =   ScheduledThreadPool.getInstance().getScheduledExecutorService();
    
    
    //单例对象
    private static  ConfigProxy2  _instance;
    
    private PropConfigurations  proxy;
    
    private static long initialDelay = 1;
    
    private static  long delay  =10*60;
    
    private  ConfigProxy2 (){
        init();
    }
 
    
    public static ConfigProxy2 getInstance(){
        
        if (_instance == null){
            synchronized (ConfigProxy2.class){
                if (_instance == null){
                    _instance  = new ConfigProxy2();
                    System.out.println("_instance");
                 }
            }
        }
      return   _instance;
    }
    
    public  ConcurrentHashMap  <String, Host>   getProxy(){
    	 scheduledService.scheduleWithFixedDelay( new Runnable (){
             @Override
             public void run() {
                 List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>(); 
                 System.out.println("check");
                 System.out.println("check");
                 Entry<String, Host> []  proxyArray =  ( Entry<String, Host>[]) HOST_MAP.entrySet().toArray();
                 System.out.println("check");
                 int index =0;
                 for (index =0; index < proxyArray.length; index++ ){
                      final Entry<String, Host> entry = proxyArray[index];
                     resultList.add( checkService.submit(new Callable<Boolean>() {
                          @Override
                         public Boolean call() throws Exception {
                               return Boolean.valueOf(ProxyHelper.testProxy(entry.getValue().getIp(), entry.getValue().getPort())) ;
                        }
                     }));
                     System.out.println("check");
                 }
                 for (index =0; index < resultList.size(); index++ ){
                     Entry<String, Host> current = proxyArray[index];
                     Future<Boolean> fs  =   resultList.get(index);
                     try {  
                         Boolean checkOk = fs.get(2, TimeUnit.SECONDS);
                         if (!checkOk){
                             HOST_MAP.remove(current.getKey());
                         }
                     } catch (InterruptedException e) {  
                         e.printStackTrace();  
                     } catch (ExecutionException e) {  
                     
                     }  catch (TimeoutException e) {
                         //超时删除
                         HOST_MAP.remove(current.getKey());
                     }
                 }  
                 
                 
             }},initialDelay, delay, TimeUnit.SECONDS);
         
              return HOST_MAP;
      }
    
    
    public  Host  getRandomHost(){
        Entry<String, Host>  [] entryArray =( Entry<String, Host>[]) HOST_MAP.entrySet().toArray();
        Entry<String, Host> random = entryArray[RandomUtil.getRandomInt(entryArray.length)];
        return random.getValue();
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
         
        log.info("initConfigProxy over!" );
    }
    
    public static void main (String [] args){
        ConfigProxy2.getInstance().getProxy();
        System.out.println("xx");
    }
    
    
}

