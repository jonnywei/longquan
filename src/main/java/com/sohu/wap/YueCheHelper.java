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
    private static String[]  AM_PM_NUM ={"812","15","58"};
    private static String[]  AM_PM_STR={"上午","下午","晚上"};
    public static Map<String, String> AMPM = new HashMap<String, String>();
    static  {
    	for(int i =0; i< AM_PM_NUM.length; i++){
    		AMPM.put(AM_PM_NUM[i], AM_PM_STR[i]);
    		AMPM.put(AM_PM_STR[i], AM_PM_NUM[i]);
    	}
    	
    }
    
    public static  int   MIN_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.min.interval", 60);
    
    public static  int   MAX_SCAN_INTEVAL = SystemConfigurations.getSystemIntProperty("system.scan.max.interval", 180);
    
   
    public static  int   MAX_SLEEP_TIME = SystemConfigurations.getSystemIntProperty("system.maxsleeptime", 3);
    
    public static String YUCHE_TIME = SystemConfigurations.getSystemStringProperty("system.yueche.time","812,15") ;
    
    private static String    SERVICE_BEGIN_TIME ="07:40";
    
    private static String   SERVICE_END_TIME ="18:00";
    
    public  static int     WAITTING_SCAN_INTERVAL= 5;
    
    
    public static   boolean isInServiceTime(){
    	
        return   DateUtil.isCurrTimeInTimeInterval(SERVICE_BEGIN_TIME,SERVICE_END_TIME);
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
