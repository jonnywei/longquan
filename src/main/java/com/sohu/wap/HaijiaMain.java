package com.sohu.wap;



import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.SystemConfigurations;



/**
 * 程序启动
 *
 */
public class HaijiaMain 
{
    private static Logger log = LoggerFactory.getLogger(HaijiaMain.class);
    private static String[]  AM_PM ={"812","15","58"};
    
    private static  int     MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        String userName = SystemConfigurations.getSystemStringProperty("system.username","411326198509012412")  ;
        String passwd =SystemConfigurations.getSystemStringProperty("system.password","0901")  ;
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
          
        System.out.println(date);
   
        doLogin(userName,passwd);
        
//        doYuche(date);
        
        System.out.println("请按任意键退出程序!");
        System.in.read();
      
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
                
                  int  result  = YueChe.yuche(date, amPm,false);
                  if (result == YueChe.BOOK_CAR_SUCCESS){
                      isSuccess = true;
                      System.out.println("约车成功");
                      log.info("约车成功");
                  
                  }else if (result == YueChe.NO_CAR){  //无车
                      System.out.println("无车。约车失败！");
                      break;
                  }
                  
                 }while (!isSuccess);
                
                
                
            }
         
        }
        
      
        log.info("yuche finish !");
        return ;
    }
    
    
    
    
}
