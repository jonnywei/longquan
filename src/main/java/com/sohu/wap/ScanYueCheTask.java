package com.sohu.wap;

import java.io.IOException;

import com.sohu.wap.bo.Result;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;

public class ScanYueCheTask extends YueCheTask {

	public static int SCAN_YUECHE_SUCCESS = 0;
	public static int ALREADY_YUECHE  =1;
	public static int NO_CAR = 2;
	
	boolean isLogon = false;
	long  lastLoginTime = 0;
	
	
	public ScanYueCheTask(XueYuanAccount xueYuan) {
		super(xueYuan, xueYuan.getYueCheDate());
		 
		// TODO Auto-generated constructor stub
	}
	/**
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scan() throws InterruptedException{
		
		doLogin () ;
		
		int yueCheInfo = canYueChe(xueYuan.getYueCheDate(),xueYuan.getYueCheAmPm());
		
		if (yueCheInfo == 0){
			doYueche(YueCheHelper.AM_STR);
		} else if (yueCheInfo == 1){
			doYueche(YueCheHelper.PM_STR);
			
		}else if (yueCheInfo == 2){
			doYueche(YueCheHelper.NI_STR);
		}else if (yueCheInfo == 3){
			return ALREADY_YUECHE;
			
		}else if (yueCheInfo == 4){
		
		}else if (yueCheInfo == 5){
			isLogon =false;
		}
		
		if (xueYuan.isBookSuccess()){
			return SCAN_YUECHE_SUCCESS;
		}
		return NO_CAR;
	}
	
	private  void  doLogin () {
		
		long currentTime = System.currentTimeMillis();
		
		//为了加快访问速度，只加载一次login页面
		//如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
		if ( ! isLogon || (currentTime - lastLoginTime > YueCheHelper.LOGIN_SESSION_TIMEOUT_MILLISECOND) ){
			boolean  isLoginSuccess = false;
	        boolean first = true;
	        do {
	             if (!first){
	                 log.error("login error. retry!");
	                 ThreadUtil.sleep(  RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
	             }else{
	                 first = false;
	             }
	             
	             isLoginSuccess =  login(xueYuan.getUserName() , xueYuan.getPassword());
	            
	       }while (!isLoginSuccess);
	        
	        isLogon = true;
	        lastLoginTime = System.currentTimeMillis();
		}
        
        log.info("login success!");
       return ;
    }
    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYueche ( String amPm ) throws InterruptedException {
    

    	  //按情况约车
        amPm = YueCheHelper.AMPM.get(amPm);
        boolean  isSuccess = false;
        boolean first = true;
        do {
             if (!first){
                 log.error("yuche  error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
        
             Result ret =  yuche(date, amPm,false);
             int  result  = ret.getRet();
             
          if (result == YueChe.BOOK_CAR_SUCCESS){
              isSuccess = true;
              String info = xueYuan.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(amPm)+"约车成功";
              System.out.println(info);
              log.info(info);
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
        
        
        
    
     
    
       log.info("yuche finish !");
        return ;
    }
}
