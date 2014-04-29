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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.VerifyCode;
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

    public  static void doYueChe(String time) throws InterruptedException {


           //初始化confighttpproxy
            ConfigHttpProxy.start();

            List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();  
            
            //约车日期
            String date =  DateUtil.getFetureDay(7);
            String dateModel = NetSystemConfigurations.getSystemStringProperty("system.yueche.date.model", "auto");
            
            if(dateModel.equals("config")){
            	 date = NetSystemConfigurations.getSystemStringProperty("system.yueche.date", DateUtil.getFetureDay(7));
            }
            
            System.out.println("抢车日期为:"+ date);
//           
            if (YueCheHelper.isEnterCreakerModel()){
              //  进入破解模式
              //  速度肯定是最快的了
              //  利用海驾的验证码漏洞，事先输入验证码，之后约车
                System.out.println("Open Creak Model");
                log.info("Open Creak Model");
                CookieImgCodeHelper.getVerifyCodeSmart(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE);
            }
          
            long startTime = System.currentTimeMillis();
            
            YueCheInfo  ycInfo = new YueCheInfo();
            
            //初始化线程池的数目
            ExecutorService executeService = Executors.newFixedThreadPool(2 + ycInfo.getYueCheInfo().size() * YueCheHelper.getProxyNumPreUser());
            
            for (Integer accoutId: ycInfo.getYueCheInfo().keySet()){
                XueYuanAccount  xy = ycInfo.getYueCheInfo().get(accoutId);
                if ( xy!=null){
                	int threadPerUserNum = 
                		YueCheHelper.isUseProxy()? YueCheHelper.getProxyNumPreUser(): YueCheHelper.getThreadPerUser();
                    
                	for ( int num = 0 ; num < threadPerUserNum; num++){
                            YueCheTask yueCheTask = new YueCheTask(xy,date);
                            resultList.add(executeService.submit(yueCheTask) );     
                       
                    } 
                 }
            }
            
            executeService.shutdown(); 
            
            //等待两个小时
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
            log.info(date+" "+time+"task execute time ="+(System.currentTimeMillis()- startTime));

            log.info(date+ time + "taskover!");

        }
    	


    private static  void doYueChe14(){

    }



    private static void doYueChe15(){

    }
   
    
    
    
}
