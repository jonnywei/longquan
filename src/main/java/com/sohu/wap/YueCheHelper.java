package com.sohu.wap;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public class YueCheHelper 
{
    private static Logger log = LoggerFactory.getLogger(YueCheHelper.class);
    
    private static String  IMAGE_CODE_INPUT_METHOD =SystemConfigurations.getSystemStringProperty("system.imagecode.inputmethod","auto") ;
    
    public static boolean IMAGE_CODE_INPUT_METHOD_IS_AUTO = true;
    
    public static String AM_STR="am";
    public static String PM_STR="pm";
    public static String NI_STR="ni";
    
    public  static String CAR_TYPE_ALS = "als";
    public  static String CAR_TYPE_BYD = "byd";
    public  static String CAR_TYPE_ZDD = "zdd";
    public  static String CAR_TYPE_FK = "fk";
    public  static String CAR_TYPE_STN = "stn";
    
    
    
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
    	
    	if(! IMAGE_CODE_INPUT_METHOD.equals("auto")){
    		IMAGE_CODE_INPUT_METHOD_IS_AUTO = false;
    	}
    	
    	
    }
    
    public static  int   MIN_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.min.interval", 60);
    
    public static  int   MAX_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.max.interval", 180);
    
   
    public static  int   MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    public static String YUCHE_TIME = SystemConfigurations.getSystemStringProperty("system.yueche.time","am,pm") ;
    
     
    public static boolean    IS_ENTER_CREAKER_MODEL = SystemConfigurations.getSystemBooleanProperty("system.open.creak.model",false) ;; 
    
    private static String    FK_YUECHE_BEGIN_TIME = "07:40";
    
    
    private static String    CREAK_START_TIME ="07:34";
    
    private static String    SERVICE_BEGIN_TIME ="07:35";
    
    private static String   SERVICE_END_TIME ="20:00";
    
    public  static int     WAITTING_SCAN_INTERVAL= 5;
    
    public static  int LOGIN_SESSION_TIMEOUT_MILLISECOND  =  30 * 60 *1000;
    
    public static  int IMAGE_CODE_TIMEOUT_MILLISECOND  =  10 * 60 *1000;
    
   
  public static   boolean isInServiceTime(){
    
      if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
          return  DateUtil.isCurrTimeInTimeInterval(CREAK_START_TIME,SERVICE_END_TIME);
      }
	  return   isInServiceTime(null);
    	 
        
    }
    
    public static   boolean isInServiceTime(String carType){
    	
    	if (carType == null || ( !CAR_TYPE_FK.equalsIgnoreCase(carType) && !CAR_TYPE_STN.equalsIgnoreCase(carType)) ){
    		
    		return   DateUtil.isCurrTimeInTimeInterval(SERVICE_BEGIN_TIME,SERVICE_END_TIME);
    		
    	}else{
    		return   DateUtil.isCurrTimeInTimeInterval(FK_YUECHE_BEGIN_TIME,SERVICE_END_TIME);
    	}
        
    }
    
    public static  void waitForService(){
    	 // 选择周六周末
        do {
            //在服务时间内
            if (!YueCheHelper.isInServiceTime()){
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }else{
            	break;
            }
        }while (true);
    }
}