



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.XueYuanAccount;
import com.sohu.wap.YueCheHelper;
import com.sohu.wap.YueCheInfo;
import com.sohu.wap.YueCheTask;

import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.NetSystemConfigurations;
import com.sohu.wap.util.ThreadPool;
import com.sohu.wap.util.ThreadUtil;



/**
 * 约车主程序
 *
 */
public class ThreadExecuteTest 
{
    
    private static Logger log = LoggerFactory.getLogger(ThreadExecuteTest.class);
    
    
   
    public static void main( String[] args ) throws InterruptedException, IOException
    {

        
        while(true){
           
            YueCheHelper.IS_USE_PROXY =false;
           
          
            //约车日期
            String date =  DateUtil.getFetureDay(7);
            String dateModel = NetSystemConfigurations.getSystemStringProperty("system.yueche.date.model", "auto");
            
            if(dateModel.equals("config")){
            	 date = NetSystemConfigurations.getSystemStringProperty("system.yueche.date", DateUtil.getFetureDay(7));
            }
            
            System.out.println("data:"+ date);
//           
            ExecutorService executeService = Executors.newFixedThreadPool(0);
            List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
            
          
            CallTest call = new   CallTest();
            
            resultList.add(  executeService.submit(call));
            
            executeService.shutdown(); 
            System.out.println("awaitTermination:"+ date);
            executeService.awaitTermination(10,TimeUnit.SECONDS);
            System.out.println("shutdown");
            executeService.shutdownNow();
            
            for (Future<Integer> fs : resultList) {  
                try {  
                    System.out.println(fs.get(2,TimeUnit.SECONDS));   // 打印各个线程（任务）执行的结果  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                } catch (ExecutionException e) {  
                    executeService.shutdownNow();  
                    e.printStackTrace();  
                    return;  
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  
            }  
            
          
            int i =0;
            while(i< 10){
                System.out.println("w");
                i++;
                Thread.sleep(1000); 
            }
            
          
             i =0;
            while(i< 10){
                System.out.println("shutdown after");
                i++;
                Thread.sleep(1000); 
            }
            
            return;
        }
    	
        
      
    }
    
   
    
    
    
}
