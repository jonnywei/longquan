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
    
   
    
    
    
    private  static int   IN_SERVICE_SCANNER_BASE = 120;
    
    private  static int   IN_SERVICE_SCANNER_INTERVAL = 180;
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
          
        System.out.println(date);
   
//        doLogin(userName,passwd);
//        
//        doYuche(date);
        
        System.in.read();
      
    }
    
    public  static void scan (){
        
        // 选择周六周末
        do {
            //在服务时间内
            if (YueCheHelper.isInServiceTime()){
            	
            	for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
                    XueYuanAccount  xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
                    if ( xy!=null){
//                    	YueCheThread yueCheTask = new YueCheThread(xy,date);
//                    	resultList.add(executeService.submit(yueCheTask) );
                    }
                }
            	
                ThreadUtil.sleep(IN_SERVICE_SCANNER_BASE + RandomUtil.getRandomInt(IN_SERVICE_SCANNER_INTERVAL)); 
                
                
            }else{
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }
        }while (true);
        
      
        
       
    }
    
    
    
   
    
    
    
     
    
    
    
}
