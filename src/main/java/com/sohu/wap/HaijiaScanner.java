package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;



/**
 * 
 * 扫描约车和约考程序
 * 
 *
 */
public class HaijiaScanner 
{
    
    
    private static Logger log = LoggerFactory.getLogger(HaijiaScanner.class);
 
    private  static int   IN_SERVICE_SCANNER_BASE = 20;
    
    private  static int   IN_SERVICE_SCANNER_INTERVAL = 60;
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        YueCheHelper.IS_USE_PROXY = false;
        
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
                    try{
                    	yueCheTask.scan();
              
                    }catch(Exception ex){
                    	log.error("exception",ex);
                    }
                     ThreadUtil.sleep(  RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
                }
            	
                ThreadUtil.sleep(IN_SERVICE_SCANNER_BASE + RandomUtil.getRandomInt(IN_SERVICE_SCANNER_INTERVAL)); 
                
                
            }else{
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }
        }while (true);
        
      
        
       
    }
    
    
    
   
    
    
    
     
    
    
    
}
