package com.sohu.wap;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sohu.wap.proxy.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.util.DateUtil;
import com.sohu.wap.util.NetSystemConfigurations;
import com.sohu.wap.util.ThreadUtil;



/**
 * 约车主程序
 *
 */
public class LongQuanNetMain
{
    
    private static Logger log = LoggerFactory.getLogger(LongQuanNetMain.class);
    
    
   
    public static void main( String[] args ) throws InterruptedException, IOException
    {

        int status = YueCheHelper.STATUS_DISPATCH;

        //初始化confighttpproxy
        ConfigHttpProxy.start();

        while(true){

             if (status == YueCheHelper.STATUS_DISPATCH) {   //启动进入的状态
                 boolean isShutdown = NetSystemConfigurations.getSystemBooleanProperty("system.client.shutdown", false);
                 if (isShutdown){
                     System.out.println("程序结束!请按任意键退出程序!");
                     log.info("System.exit!");
                     System.exit(0);
                 }

                if(YueCheHelper.isInWaitTime()){

                    status = YueCheHelper.STATUS_WAIT;

                }else if (YueCheHelper.isInQiang14ServiceTime()){

                    status =  YueCheHelper.STATUS_QIANG_14;

                }else if (YueCheHelper.isInQiang15ServiceTime()){

                    status =  YueCheHelper.STATUS_QIANG_15;
                }

             } else if (status == YueCheHelper.STATUS_WAIT){
                 System.out.println("waitting task begin!");
                 ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
                 status = YueCheHelper.STATUS_DISPATCH;  //休眠完毕，重新调度

             } else if (status == YueCheHelper.STATUS_QIANG_14){
                 doYueChe14();
                 status = YueCheHelper.STATUS_DISPATCH;  //休眠完毕，重新调度

             }else if (status == YueCheHelper.STATUS_QIANG_15){
                 doYueChe15();
                 System.out.println("今日任务已经完成！waitting tomorrow...");
                 status = YueCheHelper.STATUS_DISPATCH;  //休眠完毕，重新调度
             }

        }

    }

    public  static void doYueChe(String carType) throws InterruptedException {

            List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
            
            //约车日期
            String date =  DateUtil.getFetureDay(13);
            String dateModel = NetSystemConfigurations.getSystemStringProperty("system.yueche.date.model", "auto");
            
            if(dateModel.equals("config")){
            	 date = NetSystemConfigurations.getSystemStringProperty("system.yueche.date", DateUtil.getFetureDay(13));
            }
            
            System.out.println("抢车日期为:"+ date);

        long startTime = System.currentTimeMillis();
            

        YueCheInfo  ycInfo = new YueCheInfo(carType, date);
            
            //初始化线程池的数目
        int threadPerUserNum =
                YueCheHelper.isUseProxy()? YueCheHelper.getProxyNumPreUser(): YueCheHelper.getThreadPerUser();

        ExecutorService executeService = Executors.newFixedThreadPool(2 + ycInfo.getYueCheInfo().size() * threadPerUserNum );
            
            for (Integer accoutId: ycInfo.getYueCheInfo().keySet()){
                YueCheItem xy = ycInfo.getYueCheInfo().get(accoutId);
                if ( xy!=null){
                	for ( int num = 0 ; num < threadPerUserNum; num++){
                        Host proxyHost = null;
                         if (YueCheHelper.isUseProxy() ){
                              proxyHost = ConfigHttpProxy.getInstance().getRandomHost();
                         }
                        YueCheTask yueCheTask = new YueCheTask(xy,date,proxyHost );
                        resultList.add(executeService.submit(yueCheTask) );

                    } 
                 }
            }
            
            executeService.shutdown();
            //等待30分钟
            executeService.awaitTermination(30*60,TimeUnit.SECONDS);
         
            System.out.println("shutdown Now!");
            
            executeService.shutdownNow();
            
            for (Future<Integer> fs : resultList) {  
                try {  
                    System.out.println(fs.get(2,TimeUnit.SECONDS));   // 打印各个线程（任务）执行的结果  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                } catch (ExecutionException e) {  
                    e.printStackTrace();  
               } catch (TimeoutException e) {
                     e.printStackTrace();
                }  
            }  
            log.info(date+" "+carType+"task execute time ="+(System.currentTimeMillis()- startTime));

            log.info(date + carType + "taskover!");

        }
    	


    //14点桑塔纳
    private static  void doYueChe14(){
        try {
            doYueChe("stn");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("doYueChe14");
//        try {
//            ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }


     //15点奇瑞旗云
    private static void doYueChe15(){

        System.out.println("doYueChe15");
        try {
            doYueChe("qy");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//
//        try {
//            ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
   
    
    
    
}
