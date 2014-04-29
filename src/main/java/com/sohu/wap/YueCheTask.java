package com.sohu.wap;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.Result;
import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.proxy.ConfigHttpProxy;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;

public class YueCheTask  extends YueChe implements Callable<Integer> {

	protected static Logger log = LoggerFactory.getLogger(YueCheTask.class);
	
	XueYuanAccount xueYuan;
	
	String date;
	YueCheTask(){
	    
	}
	
	
	public YueCheTask(XueYuanAccount xueYuan, String date){

        httpUtil4 = HttpUtil4Exposer.createHttpClient();
//	    if (YueCheHelper.isUseProxy()){
//	             Host host = ConfigHttpProxy.getInstance().getRandomHost();
//
//	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(),host.getPort());
//	    }else{
//	    		 httpUtil4 = HttpUtil4Exposer.createHttpClient();
//	    }
	
		this.xueYuan = xueYuan;
		this.date = date; 
		log.info(this.xueYuan.getUserName()+" init thread");
	}
	
	@Override
	public Integer call()  throws Exception {
	    log.info(this.xueYuan.getUserName()+"  yueche thread start!");
		 try {
//            YueCheHelper.waiting(xueYuan.getCarType());
            
            if (doLogin() ==0){ 
                doYuche();
            }
            if (xueYuan.isBookSuccess()){
                return Integer.valueOf(0);
             }
            
        } catch (InterruptedException e) {
              log.error("cancel task! ");
              return Integer.valueOf(2);
        }  
        return Integer.valueOf(1);
        
	}
	
	/**
	 *
	 *0 登录成功
	 *1 登录失败
	 *2 已经约车成功
	 *3 无法进行下一步了
	 * */
	
	private  int   doLogin () throws InterruptedException{
        
		boolean  isLogin = false;
        boolean first = true;
        int count =0;
        
        
        do {
             if (!first){
                 log.error("login error. retry!");
                 ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
             
             int loginResult = login(xueYuan.getUserName() , xueYuan.getPassword());
             
             if(loginResult == YueChe.LONGIN_PROXY_ERROR || loginResult == YueChe.LONGIN_IP_FORBIDDEN ){
            	 log.error("proxy error or ip forbidden !");
            	 return 3;
             } else if( loginResult == YueChe.LONGIN_ACCOUNT_ERROR ){
            	 String info ="accountError:"+xueYuan.getUserName()+","+xueYuan.getPassword();
            	 log.error(info); 
            	 YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_ACCOUNT_ERROR, info);
            	 return 3;
             }else if( loginResult == YueChe.LONGIN_SUCCESS ){
            	 isLogin = true;
             }else{
            	 log.error("login error!"); 
             }
             
             count ++;
             if(count % 10 == 0){
            	int ycResult =  YueCheHelper.getYueCheBookInfo(xueYuan.getId());
            	if(ycResult == XueYuanAccount.BOOK_CAR_SUCCESS || 
            			ycResult == XueYuanAccount.BOOK_CAR_ALREADY_BOOKED_CAR ){
            		log.info(xueYuan.getUserName()+"已经约车成功.");
            		return 2;
            	}
             }
             
       }while (!isLogin);
        log.info("login success!");
       return 0;
    }
    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYuche () throws InterruptedException {
        //am,pm,ni  to 812,15,58
        String shiDuan = YueCheHelper.transformYueCheShiDuanToNum(xueYuan.getYueCheAmPm());

        int count =0;

        boolean  isSuccess = false;        //按情况约车
        boolean first = true;

        do {
            count ++; //十次就测试
            if(count % 10 == 0){
                int ycResult =  YueCheHelper.getYueCheBookInfo(xueYuan.getId());
                if(ycResult == XueYuanAccount.BOOK_CAR_SUCCESS ||
                        ycResult == XueYuanAccount.BOOK_CAR_ALREADY_BOOKED_CAR ){
                    isSuccess =true;
                    break;
                }
            }

            if (!first){
                log.error("yuche  error. retry !");
                ThreadUtil.sleep( RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
            }else{
                first = false;
            }

            Result<String> ret =  null;
            //判断科目信息
            if(Constants.KM3.equals(xueYuan.getKm())){
                ret =  yuche(date, shiDuan,Constants.KM3_HiddenKM, xueYuan.getPhoneNum());
            }else if (Constants.KM1.equals(xueYuan.getKm())){
                ret =  yuche(date, shiDuan,Constants.KM1_HiddenKM, xueYuan.getPhoneNum());
            }else if (Constants.KM_AUTO.equals(xueYuan.getKm())) {
                ret =  yuche(date, shiDuan,0, xueYuan.getPhoneNum());
            }else{
                ret =  yuche(date, shiDuan,Constants.KM2_HiddenKM, xueYuan.getPhoneNum());
            }

            String uinfo = xueYuan.getUserName() +":"+date+ YueCheHelper.AMPM.get(shiDuan);

            int  result  = ret.getRet();

            if (result == YueChe.BOOK_CAR_SUCCESS){
                isSuccess = true;
                uinfo = xueYuan.getUserName() +":"+ret.getData()+":"+date+ YueCheHelper.AMPM.get(shiDuan);
                String info =uinfo +"约车成功！";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_SUCCESS, info);
                xueYuan.setBookSuccess(isSuccess);
                break;
            }else if (result == YueChe.ALREADY_BOOKED_CAR){  //该日已经预约车辆
                String info = uinfo+ "该日已经预约车辆了！";
                log.info(info);
                System.out.println(info);
                isSuccess = true;
                xueYuan.setBookSuccess(isSuccess);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_ALREADY_BOOKED_CAR, info);
                break;
            }else if (result == YueChe. KEMU2_NO_TIME){  //无车
                String info = uinfo +"科目二剩余小时不足!";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_KEMU2_NO_TIME, info);
                return ;
            } else if (result == YueChe. PHONE_NUM_ERROR){  //对不起,您填写的报名时预留的手机或固定电话号码不正确
                String info = uinfo +"对不起,您填写的报名时预留的手机或固定电话号码不正确";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_PHONE_NUM_ERROR, info);
                return ;
            }

            else if (result == YueChe.NO_CAR){  //无车的话，赶紧约下班车
                if (YueCheHelper.isInQiang15ServiceTime()){
                    System.out.println(uinfo+"无车!continue qiang che");
                    log.info("nocar but in servicetime ,continue qiang");
                } else{
                    break;
                }

            }else if (result == YueChe. BOOK_INVAILD_OPERATION || result ==YueChe.IP_FORBIDDEN ){  //非法操作，服务器已经被锁定，直接退出约车
                String info = uinfo +"非法操作!";
                System.out.println(info);
                log.info(info);
                return;
            }else if (result == YueChe. NOT_BOOK_WEEKEND_CAR){  //所在班种不能约周六日车辆
                String info = uinfo +"所在班种不能约周六日车辆";
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_NOT_BOOK_WEEKEND_CAR, info);
                return;
            } else if (result == YueChe.CAR_TYPE_ERROR){  //您不能预约该车型的车
                String info = uinfo +"您不能预约该车型的车!";
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(xueYuan.getId(), XueYuanAccount.BOOK_CAR_CAR_TYPE_ERROR, info);
                return;
            }  else if (result == YueChe.GET_CAR_ERROR){   //得到车辆信息错误的话
                log.info("get car info error ! retry!");
                System.out.println("得到车辆信息错误！重试！");
            }else if (result == YueChe.GET_BOOK_CODE_ERROR){   //得到book code错误的话
                log.info("GET_BOOK_CODE_ERROR");
                System.out.println("得到车辆信息错误！重试！");
            }
            else {  //无车
                System.out.println("未知错误！重试! RESULT="+result);
            }

        } while (true);

        if (isSuccess){  //如果约车成功的话，退出
//            break;
        }


        log.info("yuche finish!");
        return ;
    }
    
    
    

}
