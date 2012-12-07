package com.sohu.wap;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.Result;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;

public class YueCheTask  extends YueChe implements Callable<Integer> {

	protected static Logger log = LoggerFactory.getLogger(YueCheTask.class);
	
	XueYuanAccount xueYuan;
	
	String date;
	
	public YueCheTask(XueYuanAccount xueYuan, String date){
	  

	    if (YueCheHelper.IS_USE_PROXY){
	            Host host = ConfigHttpProxy.getInstance().getRandomHost();
	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(),host.getPort());
	    }else{
	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient();
	    }
	
		this.xueYuan = xueYuan;
		this.date = date; 
	}
	
	@Override
	public Integer call() throws Exception {
	    
		 YueCheHelper.waiting(xueYuan.getCarType());
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
                 ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
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
    
    	String[] timeArray = xueYuan.getYueCheAmPm().split("[,;]");
        if (timeArray.length  <  0) {
        	timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
        }
        
        for (String amPm1 : timeArray){  //按情况约车
            String amPm  =  YueCheHelper.AMPM.get(amPm1);
            boolean  isSuccess = false;
            boolean first = true;
            do {
                 if (!first){
                     log.error("yuche  error. retry!");
                     ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
                 }else{
                     first = false;
                 }
               Result<String> ret =  yuche(date, amPm,false);
               
               String uinfo = xueYuan.getUserName() +":"+date+ YueCheHelper.AMPM.get(amPm);
               
              int  result  = ret.getRet();
              if (result == YueChe.BOOK_CAR_SUCCESS){
                  isSuccess = true;
                  uinfo = xueYuan.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(amPm);
                  String info =uinfo +"约车成功";
                  System.out.println(info);
                  log.info(info);
                  xueYuan.setBookSuccess(isSuccess);
              }else if (result == YueChe.NO_CAR){  //无车
                  System.out.println(uinfo+"无车!");
                  break;
              }else if (result == YueChe.GET_CAR_ERROR){  //无车
                  System.out.println("得到车辆信息错误！重试！");
              }else if (result == YueChe.ALREADY_BOOKED_CAR){  //无车
            	  String info = uinfo+ "该日已经预约车辆。不能在约车了！";
            	  log.info(info);
                  System.out.println(info);
                  break;
              }else if (result == YueChe. KEMU2_NO_TIME){  //无车
                  String info = uinfo +"科目二剩余小时不足!";
                  System.out.println(info);
                  log.info(info);
                  break;
              }  else {  //无车
                  System.out.println("未知错误！重试! RESULT="+result);
              }
              
             }while (!isSuccess);
            
            if (isSuccess){ //如果约车成功的话，退出
            	break;
            }
            
        }    
        
       log.info("yuche finish !");
        return ;
    }
    
    
    

}
