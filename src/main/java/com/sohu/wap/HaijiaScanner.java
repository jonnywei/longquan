package com.sohu.wap;



import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadUtil;



/**
 * 程序启动
 *
 */
public class HaijiaScanner 
{
    
    
    
    private static Logger log = LoggerFactory.getLogger(HaijiaScanner.class);
   
//    private static String[]  AM_PM ={"812","15","58"};
    
    private static String    SERVICE_BEGIN_TIME ="07:35";
    
    private static String   SERVICE_END_TIME ="18:00";
    
    
    private static  int     MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    
    private  static int   NOT_IN_SERVICE_SCANNER_INTERVAL= 5;
    
    private  static int   IN_SERVICE_SCANNER_BASE = 120;
    
    private  static int   IN_SERVICE_SCANNER_INTERVAL = 180;
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        String userName = SystemConfigurations.getSystemStringProperty("system.username","411326198509012412")  ;
        String passwd =SystemConfigurations.getSystemStringProperty("system.password","0901")  ;
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
          
        System.out.println(date);
   
        doLogin(userName,passwd);
        
        doYuche(date);
        
        System.in.read();
      
    }
    
    public  static void scan (){
        
        // 选择周六周末
        do {
            //在服务时间内
            if (isInServiceTime()){
                
                ThreadUtil.sleep(IN_SERVICE_SCANNER_BASE + RandomUtil.getRandomInt(IN_SERVICE_SCANNER_INTERVAL)); 
            }else{
                ThreadUtil.sleep(NOT_IN_SERVICE_SCANNER_INTERVAL);
            }
        }while (true);
        
      
        
       
    }
    
    
    
    private static   boolean isInServiceTime(){
      return   DateUtil.isCurrTimeInTimeInterval(SERVICE_BEGIN_TIME,SERVICE_END_TIME);
    }
    
    
    
    private static void  doLogin (String userName, String passwd ) throws InterruptedException{
        boolean  isLogin = false;
        boolean first = true;
      do {
             if (!first){
                 log.error("login error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
             
             isLogin =  YueChe.login(userName , passwd);
            
       }while (!isLogin);
        log.info("login success!");
       return ;
    }
    
    /**
     * @throws IOException 
     * 
     */
    private static void  doYuche (String date  ) throws InterruptedException, IOException{
      
        String ycTime = SystemConfigurations.getSystemStringProperty("system.yueche.time","812,15") ;
        String[] timeArray = ycTime.split("[,;]");
        if (timeArray.length > 0) {
            for (String amPm : timeArray){  //按情况约车
               
                boolean  isSuccess = false;
                boolean first = true;
                do {
                     if (!first){
                         log.error("yuche  error. retry!");
                         Thread.sleep(1000 * RandomUtil.getRandomInt(MAX_SLEEP_TIME));
                     }else{
                         first = false;
                     }
                
                  int   result  = YueChe.yuche(date, amPm,false);
                  if (result == 0){
                      isSuccess = true;
                  }else if (result == 1){  //无车
                      System.out.println("无车。约车失败！");
                      break;
                  }
                 }while (!isSuccess);
                
                log.info("约车成功");
                
            }
         
        }
        
        
        
        log.info("login success!");
       return ;
    }
    
    
    
    
}
