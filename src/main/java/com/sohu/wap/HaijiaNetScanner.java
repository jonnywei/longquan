package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class HaijiaNetScanner 
{
    
    
    private static Logger log = LoggerFactory.getLogger(HaijiaNetScanner.class);

    
    public static void main( String[] args ) throws InterruptedException, IOException
    {
        YueCheHelper.IS_USE_PROXY = false;
        
        startScanService();
        
        System.in.read();
      
    }
    
    
    
    public  static void startScanService () throws InterruptedException{
        
    	//初始化约车信息
    	Map<String , ScanYueCheTask>  scanMap = new HashMap<String ,ScanYueCheTask>();
    	
    	ScanYueCheInfo scanYcInfo = new ScanYueCheInfo();
    	
    	
    	
    	//初始化线程池的数目
        ExecutorService executeService = Executors.newFixedThreadPool( scanYcInfo.getYueCheInfo().size());
//        
//        for (String accoutId: scanYcInfo.getYueCheInfo().keySet()){
//            XueYuanAccount  xy = scanYcInfo.getYueCheInfo().get(accoutId);
//            if ( xy!=null){
//            	int threadPerUserNum = 
//            		YueCheHelper.isUseProxy()? YueCheHelper.getProxyNumPreUser(): YueCheHelper.getThreadPerUser();
//                
//            	for ( int num = 0 ; num < threadPerUserNum; num++){
//                        YueCheTask yueCheTask = new YueCheTask(xy,date);
//                        resultList.add(executeService.submit(yueCheTask) );     
//                   
//                } 
//             }
//        }
    	
    	
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
                     ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.SCAN_MAX_SLEEP_TIME));
                }
            	//max >休息时间 >min
                ThreadUtil.sleep(YueCheHelper.SCAN_MIN_INTEVAL + 
                		RandomUtil.getRandomInt(YueCheHelper.SCAN_MAX_INTEVAL - YueCheHelper.SCAN_MIN_INTEVAL)); 
                
            }else{
            	log.info("waiting");
                ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
            }
        }while (true);
        
      
        
       
    }
    
    
    
   
    
    
    
     
    
    
    
}
