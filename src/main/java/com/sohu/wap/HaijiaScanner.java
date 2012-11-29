package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        
        
    	scan();
        
        System.in.read();
      
    }
    
    public  static void scan () throws InterruptedException{
        
    	List <ScanYueCheTask> list = new ArrayList<ScanYueCheTask>();
    	for (String accoutId: AccountMap.getInstance().getScanXueYuanAccountMap().keySet()){
            XueYuanAccount  xy =AccountMap.getInstance().getScanXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
            	
            	ScanYueCheTask yueCheTask = new ScanYueCheTask(xy);
            	list.add(yueCheTask);
            }
          
        }
         
        do {
        	
        	
            //在服务时间内
            if (YueCheHelper.isInServiceTime()){
            	
            	for (ScanYueCheTask yueCheTask: list){
                     
            		yueCheTask.scan();
                    
                    ThreadUtil.sleep(  RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
                }
            	
                ThreadUtil.sleep(IN_SERVICE_SCANNER_BASE + RandomUtil.getRandomInt(IN_SERVICE_SCANNER_INTERVAL)); 
                
                
            }else{
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }
        }while (true);
        
      
        
       
    }
    
    
    
   
    
    
    
     
    
    
    
}
