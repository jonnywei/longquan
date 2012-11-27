/**
 *@version:2012-5-8-下午03:03:50
 *@author:jianjunwei
 *@date:下午03:03:50
 *
 */
package com.sohu.wap;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.PropConfigurations;




/**
 * 全局accoutMap 对象 
 * 
 * @author jianjunwei
 *
 */
public class AccountMap {
    
    
    
    private static Logger  log = LoggerFactory.getLogger(AccountMap.class);
    
    
    //单例对象
    private static  AccountMap  _instance;
    
  
    private    ConcurrentHashMap <String , XueYuanAccount>  xueYuanAccountMap   = new ConcurrentHashMap<String, XueYuanAccount> ();
   
    
    private PropConfigurations xueYuan;
    
    
    
    private  AccountMap (){
        init();
    }
    
 
    
    public static AccountMap getInstance(){
        
        if (_instance == null){
            synchronized (AccountMap.class){
                if (_instance == null){
                    _instance  = new AccountMap();
                }
            }
        }
      return   _instance;
    }
    

    
    
 
    
    
  public ConcurrentHashMap  <String , XueYuanAccount>   getXueYuanAccountMap(){
            return xueYuanAccountMap;
    }
  
  
    
    
    
    
    /**
     * 
     * 初始化 account 对象 
     *
     */
    private  void init (){
        
        
         xueYuan = new PropConfigurations("mapdb.properties");
      
         initXueYuanAccounts();
    }
    
    
    
    
    /**
     * 初始化搜狐account 对象 
     */
    private   void initXueYuanAccounts(   ){
     
        
       Properties mapdb =  xueYuan.getProperties();
      
 
        Iterator itor =mapdb.keySet().iterator();
   
        while(itor.hasNext())
        {
           String key = ((String)itor.next()).trim() ;
      
           String value =(String) mapdb.get(key);
        
           String temp [] =  value.split(";");
           
           String password = temp[0];

           XueYuanAccount sa= new XueYuanAccount();
         
           sa.setUserName(key);
           sa.setPassword(password);
          
           xueYuanAccountMap.put(key, sa);
//           log.info("add SohuAccounts " + sa);
         }
         
        log.info("initXuYuanAccounts over!" );
    }
    
    
    
}
