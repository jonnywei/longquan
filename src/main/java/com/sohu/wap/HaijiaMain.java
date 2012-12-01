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
 * 约车主程序
 *
 */
public class HaijiaMain 
{
    private static Logger log = LoggerFactory.getLogger(HaijiaMain.class);
   
    private static int nThreads = 10;
    
    public static void main( String[] args ) throws InterruptedException, IOException
    {

    	ExecutorService executeService = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
        
        String date = DateUtil.getFetureDay(SystemConfigurations.getSystemIntProperty("system.yueche.date",7));
      
        System.out.println("抢车日期为:"+ date);
       
        YueCheHelper.waitForService();
        
        if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
          //进入破解模式
          //  速度肯定是最快的了
          //  利用海驾的验证码漏洞，事先输入验证码，之后约车
            System.out.println("Open Creak Model");
            log.info("Open Creak Model");
            ImageCodeHelper.getImageCodeCookie();
        }
      
        
      
        
        for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
            XueYuanAccount  xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
            	YueCheTask yueCheTask = new YueCheTask(xy,date);
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
