package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.SystemConfigurations;



/**
 * 
 *
 * 
 * 
 *
 */
public class HaijiaCreaker 
{
    private static Logger log = LoggerFactory.getLogger(HaijiaCreaker.class);
   
    private static int nThreads = 10;
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {

        
    	ExecutorService executeService = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
          
        System.out.println(date);
        
     
        
        
        YueCheHelper.IS_ENTER_CREAKER_MODEL = true;
        
        CookieImgCodeHelper.getImageCodeCookie();
        
        for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
            YueCheItem xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
            	YueCheTask yueCheTask = new YueCheTask(xy,date,null);
            	resultList.add(executeService.submit(yueCheTask) );
            }
        }
        
        executeService.shutdown(); 
        
        for (Future<Integer> fs : resultList) {  
            try {  
                System.out.println(fs.get()); // 打印各个线程（任务）执行的结果  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            } catch (ExecutionException e) {  
            	executeService.shutdownNow();  
                e.printStackTrace();  
                return;  
            }  
        }  
        
        System.out.println("请按任意键退出程序!");
        System.in.read();
      
    }
    
    
   
    
    
}
