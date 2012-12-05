/**
 *@version:2012-12-5-下午07:03:38
 *@author:jianjunwei
 *@date:下午07:03:38
 *
 */
package com.sohu.wap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.sohu.wap.util.BookThreadPool;
import com.sohu.wap.util.ThreadPool;

/**
 * @author jianjunwei
 *
 */
public class BookCar {
    
    private static int MAX_BOOK_REQUEST =10;
    
    private static int BOOK_TIME_OUT =20*1000;

    public static int book (JSONObject carInfo,JSONObject cookieInfo  ){
        
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
        
        ExecutorService executeService = BookThreadPool.getInstance().getExecutorService();
        for (int i =0; i< MAX_BOOK_REQUEST; i ++){
            BookCarTask bookCarTask = new BookCarTask( carInfo, cookieInfo );
            Future<Integer> future = executeService.submit(bookCarTask);
            resultList.add(future);
        }
     
        
        for (Future<Integer> future : resultList) {  
            try {  
            
                int result = future.get(BOOK_TIME_OUT, TimeUnit.MILLISECONDS);  // 打印各个线程（任务）执行的结果  
            } catch (TimeoutException e) {
                e.printStackTrace();
            }  catch (InterruptedException e) {  
                e.printStackTrace();  
            } catch (ExecutionException e) {  
                e.printStackTrace();  
            }  
        }  
        
       return 1; 
        
    }
}
