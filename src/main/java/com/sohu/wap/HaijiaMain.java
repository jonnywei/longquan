package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadPool;



/**
 * 约车主程序
 *
 */
public class HaijiaMain 
{
    private static Logger log = LoggerFactory.getLogger(HaijiaMain.class);
   
    public static void main( String[] args ) throws InterruptedException, IOException
    {

    	ExecutorService executeService = ThreadPool.getInstance().getExecutorService();
    	
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
        
        String date = SystemConfigurations.getSystemStringProperty("system.yueche.date", DateUtil.getFetureDay(7));
      
        System.out.println("抢车日期为:"+ date);
       
        YueCheHelper.waitForService();
        
        if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
          //进入破解模式
          //  速度肯定是最快的了
          //  利用海驾的验证码漏洞，事先输入验证码，之后约车
            System.out.println("Open Creak Model");
            log.info("Open Creak Model");
            CookieImgCodeHelper.getImageCodeCookie();
        }
      
        
        for (String accoutId: AccountMap.getInstance().getXueYuanAccountMap().keySet()){
            XueYuanAccount  xy =AccountMap.getInstance().getXueYuanAccountMap().get(accoutId);
            if ( xy!=null){
                if (YueCheHelper.IS_USE_PROXY){
                    for ( int num = 0 ; num < YueCheHelper.PROXY_NUM_PER_USER; num++){
                        YueCheTask yueCheTask = new YueCheTask(xy,date);
                        resultList.add(executeService.submit(yueCheTask) );
                    }
                }else{
                    YueCheTask yueCheTask = new YueCheTask(xy,date);
                    resultList.add(executeService.submit(yueCheTask) );
                }
             }
        }
        
        executeService.shutdown(); 
        
        for (Future<Integer> fs : resultList) {  
            try {  
                System.out.println(fs.get());   // 打印各个线程（任务）执行的结果  
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
