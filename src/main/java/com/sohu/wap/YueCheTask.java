package com.sohu.wap;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.http.HttpUtil4;
import com.sohu.wap.util.RandomUtil;

public class YueCheTask  extends YueChe implements Callable<Integer> {

	protected static Logger log = LoggerFactory.getLogger(YueCheTask.class);
	
	XueYuanAccount xueYuan;
	
	String date;
	
	public YueCheTask(XueYuanAccount xueYuan, String date){
		httpUtil4 = HttpUtil4.createHttpClient();
		this.xueYuan = xueYuan;
		this.date = date; 
	}
	
	@Override
	public Integer call() throws Exception {
		 doLogin();
         doYuche();
         if (xueYuan.isBookSuccess()){
        	 return Integer.valueOf(0);
         }
         return Integer.valueOf(1);
	}
	
	
	private  void  doLogin ( ) throws InterruptedException{
        boolean  isLogin = false;
        boolean first = true;
      do {
             if (!first){
                 log.error("login error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
             
             isLogin =  login(xueYuan.getUserName() , xueYuan.getPassword());
            
       }while (!isLogin);
        log.info("login success!");
       return ;
    }
    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYuche () throws InterruptedException {
    
    	String[] timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
        if (timeArray.length > 0) {
            for (String amPm : timeArray){  //按情况约车
               
                boolean  isSuccess = false;
                boolean first = true;
                do {
                     if (!first){
                         log.error("yuche  error. retry!");
                         Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
                     }else{
                         first = false;
                     }
                
                  int  result  = yuche(date, amPm,false);
                  if (result == YueChe.BOOK_CAR_SUCCESS){
                      isSuccess = true;
                      System.out.println(date + YueCheHelper.AMPM.get(amPm)+"约车成功");
                      log.info(date +  YueCheHelper.AMPM.get(amPm)+"约车成功");
                      xueYuan.setBookSuccess(isSuccess);
                  }else if (result == YueChe.NO_CAR){  //无车
                      System.out.println(date + YueCheHelper.AMPM.get(amPm)+"无车!");
                      break;
                  }else if (result == YueChe.GET_CAR_ERROR){  //无车
                      System.out.println("得到车辆信息错误！重试！");
                  }else if (result == YueChe.ALREADY_BOOKED_CAR){  //无车
                      System.out.println(date+"该日已经预约车辆。不能在约车了！");
                      break;
                  }else {  //无车
                      System.out.println("未知错误！重试!RUSULT="+result);
                  }
                  
                 }while (!isSuccess);
                
                
                
            }
         
        }
       log.info("yuche finish !");
        return ;
    }
    
    
    

}
