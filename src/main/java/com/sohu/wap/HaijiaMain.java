package com.sohu.wap;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private static String[]  AM_PM_NUM ={"812","15","58"};
    private static String[]  AM_PM_STR={"上午","下午","晚上"};
    public static Map<String, String> AMPM = new HashMap<String, String>();
    static  {
    	for(int i =0; i< AM_PM_NUM.length; i++){
    		AMPM.put(AM_PM_NUM[i], AM_PM_STR[i]);
    		AMPM.put(AM_PM_STR[i], AM_PM_NUM[i]);
    	}
    	
    }
    private static  int     MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
//        String userName = SystemConfigurations.getSystemStringProperty("system.username","411326198509012412")  ;
//        String passwd =SystemConfigurations.getSystemStringProperty("system.password","0901")  ;
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
          
        System.out.println(date);
   
        for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
            XueYuanAccount  xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
                doLogin(xy.getUserName(),xy.getPassword());
                
                doYuche(date);
                
                YueChe.logout();
            }
        
        }
        
    
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
    private static void  doYuche (String date ) throws InterruptedException, IOException{
      
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
                      System.out.println(date + AMPM.get(amPm)+"约车成功");
                      log.info(date +  AMPM.get(amPm)+"约车成功");
                  
                  }else if (result == YueChe.NO_CAR){  //无车
                      System.out.println(date + AMPM.get(amPm)+"无车!");
                      break;
                  }else if (result == YueChe.GET_CAR_ERROR){  //无车
                      System.out.println("得到车辆信息错误！重试！");
                  }else if (result == YueChe.ALREADY_BOOKED_CAR){  //无车
                      System.out.println(date+"该日已经预约车辆。不能在约车了！");
                      break;
                  }else {  //无车
                      System.out.println("未知错误！重试!RUSULT="+result);
                  }
                  
                 }while (!isSuccess);
                
                
                
            }
         
        }
        
      
        log.info("yuche finish !");
        return ;
    }
    
    
    
    
}
