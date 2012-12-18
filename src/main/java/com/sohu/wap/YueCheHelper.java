package com.sohu.wap;



import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.NetSystemConfigurations;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadUtil;



/**
 * 程序启动
 *
 */
public class YueCheHelper 
{
    private static Logger log = LoggerFactory.getLogger(YueCheHelper.class);
    
    
    
    public static  String  getImageCodeInputMethod(){
          
        String  IMAGE_CODE_INPUT_METHOD =NetSystemConfigurations.getSystemStringProperty("system.imagecode.inputmethod","auto") ;
        return IMAGE_CODE_INPUT_METHOD;
        
    }
    
    //遗留的，删除
    public static  boolean    IS_ENTER_CREAKER_MODEL = false;
    
    public static  boolean  isImageCodeInputMethodAuto(){
        boolean IMAGE_CODE_INPUT_METHOD_IS_AUTO = true;
        if(! getImageCodeInputMethod().equals("auto")){
            IMAGE_CODE_INPUT_METHOD_IS_AUTO = false;
        }
        return IMAGE_CODE_INPUT_METHOD_IS_AUTO;
    }
    
   
    public static boolean isEnterCreakerModel(){
        boolean    IS_ENTER_CREAKER_MODEL = NetSystemConfigurations.getSystemBooleanProperty("system.open.creak.model",false) ;; 
        return IS_ENTER_CREAKER_MODEL;
    }
    
    //遗留的，删除
    public static boolean IS_USE_PROXY= false;
    
    public static boolean isUseProxy(){
        
        boolean   IS_USE_PROXY = NetSystemConfigurations.getSystemBooleanProperty("system.use.proxy",false) ;; 
        
        return IS_USE_PROXY;
    }
    
    
    public static int  getProxyNumPreUser(){
        
        int   PROXY_NUM_PER_USER = NetSystemConfigurations.getSystemIntProperty("system.proxy.num.per.user", 2);
        
        return PROXY_NUM_PER_USER;
    }
    
    
    
    
    
   
    private static Map<String, String>  IS_EXECUTE_TASK = new HashMap<String, String>();
    
    
    private static String[]  AM_PM_NUM ={"812","15","58"};
    private static String[]  AM_PM_STR={"上午","下午","晚上"};
    private static String[]  AM_PM_STR1={"sw","xw","ws"};
    private static String[]  AM_PM_STR2={"am","pm","ni"};
    public static Map<String, String> AMPM = new HashMap<String, String>();
    static  {
    	//初始化 ampm信息
    	for(int i =0; i< AM_PM_NUM.length; i++){
    		AMPM.put(AM_PM_NUM[i], AM_PM_STR[i]);
    		AMPM.put(AM_PM_STR[i], AM_PM_NUM[i]);
    		AMPM.put(AM_PM_STR1[i], AM_PM_NUM[i]);
    		AMPM.put(AM_PM_STR2[i], AM_PM_NUM[i]);
    	}
    	
    
    	
    	
    }
    
    public static  int   MIN_SCAN_INTEVAL = NetSystemConfigurations.getSystemIntProperty("system.scan.min.interval", 60);
    
    public static  int   MAX_SCAN_INTEVAL = NetSystemConfigurations.getSystemIntProperty("system.scan.max.interval", 180);
    
    public static  int   MAX_SLEEP_TIME = NetSystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    public static String YUCHE_TIME = NetSystemConfigurations.getSystemStringProperty("system.yueche.time","am,pm") ;
    
     
    
    private static String    FK_YUECHE_BEGIN_TIME = "07:40";
//    private static String    FK_YUECHE_BEGIN_TIME = "00:50";
    
    private static String    CREAK_START_TIME ="07:34";
    
    private static String    SERVICE_BEGIN_TIME ="07:35";
//    private static String    SERVICE_BEGIN_TIME ="00:49";
    
    private static String   SERVICE_END_TIME ="20:00";
    
    public  static int     WAITTING_SCAN_INTERVAL= 5;
    
    public static  int LOGIN_SESSION_TIMEOUT_MILLISECOND  =  30 * 60 *1000;
    
    public static  int IMAGE_CODE_TIMEOUT_MILLISECOND  =  10 * 60 *1000;
    
    
   public static  String   PROXY_IP = NetSystemConfigurations.getSystemStringProperty("system.proxy.ip", "127.0.0.1");
    
   public static  int   PROXY_PORT = NetSystemConfigurations.getSystemIntProperty("system.proxy.port", 8087);
    
  
   
//   public static  String   GAE_PROXY_IP = NetSystemConfigurations.getSystemStringProperty("system.gae.proxy.ip", "127.0.0.1");
//   
//   public static  int   GAE_PROXY_PORT = NetSystemConfigurations.getSystemIntProperty("system.gae.proxy.port", 8087);
//    
    
   public static boolean   IS_USE_PROXY_BOOK_CAR = NetSystemConfigurations.getSystemBooleanProperty("system.use.proxy.bookcar",false) ;; 
   
   
  public static   boolean isInServiceTime(){
    
      if (YueCheHelper.isEnterCreakerModel()){
          return  DateUtil.isCurrTimeInTimeInterval(CREAK_START_TIME,SERVICE_END_TIME);
      }
	  return   isInServiceTime(null);
    	 
        
    }
    
    public static   boolean isInServiceTime(String carType){
    	
    	if (carType == null || ( !Constants.CAR_TYPE_FK.equalsIgnoreCase(carType) && !Constants.CAR_TYPE_STN.equalsIgnoreCase(carType)) ){
    		
    		return   DateUtil.isCurrTimeInTimeInterval(SERVICE_BEGIN_TIME,SERVICE_END_TIME);
    		
    	}else{
    		return   DateUtil.isCurrTimeInTimeInterval(FK_YUECHE_BEGIN_TIME,SERVICE_END_TIME);
    	}
        
    }
    
    
    /**
     *设置今天任务已经完成 
     * 
     */
    public static void setTodayTaskExecuteOver(){
        IS_EXECUTE_TASK.put(DateUtil.getNowDay(), "over");
    }
    
    
    
    
    
    /**
     * 
     *得到今日任务完成状态
     * 
     */
    public static boolean  isTodayTaskExecuteOver(){
        
        
        if (IS_EXECUTE_TASK.containsKey(DateUtil.getNowDay())){  //今日已经执行完毕
            return true;
        }
        
        return false;
        
    }
    
    
    
    public static  void waitForService(){
    	  
        do {
            //在服务时间内
            if (!YueCheHelper.isInServiceTime()){
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }else{
            	break;
            }
        }while (true);
    }
    
    
    
    public static void waiting(String carType){
    	Date beginDate = DateUtil.getTodayTime(SERVICE_BEGIN_TIME);
    	
    	if ( Constants.CAR_TYPE_FK.equalsIgnoreCase(carType)  || Constants.CAR_TYPE_STN.equalsIgnoreCase(carType)){
    		beginDate = DateUtil.getTodayTime(FK_YUECHE_BEGIN_TIME);
    	}
    	long beginTime = beginDate.getTime();
    	
    	do {
    		long distance = beginTime - System.currentTimeMillis();
            //在服务时间内
    		System.out.println("等待...");
            if ( distance  >  YueCheHelper.MAX_SCAN_INTEVAL*1000 ){
            	ThreadUtil.sleep (YueCheHelper.MIN_SCAN_INTEVAL +RandomUtil.getRandomInt(YueCheHelper.MAX_SCAN_INTEVAL-YueCheHelper.MIN_SCAN_INTEVAL));
            	
            }else if ( distance > 10000 ){
            	ThreadUtil.sleep(MAX_SLEEP_TIME);
            }else{
            	break;
            }
        }while (true);
    }
    
    public static void main (String[] args){
    	waiting("fk");
       
    }
}
