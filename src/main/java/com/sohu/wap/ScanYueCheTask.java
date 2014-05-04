package com.sohu.wap;

import java.io.IOException;

import com.sohu.wap.bo.Result;
import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.ThreadUtil;

public class ScanYueCheTask extends YueCheTask {

	public static int SCAN_YUECHE_SUCCESS = 0;
	public static int ALREADY_YUECHE  =1;
	public static int NO_CAR = 2;
	
	boolean isLogon = false;
	long  lastLoginTime = 0;
    private XueYuanAccount xueYuanAccount;
	
	public ScanYueCheTask(XueYuanAccount xueYuan, Host host) {
	    
	    super();
	    
        if (host !=null ){

                 httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(),host.getPort());
        }else{
                 httpUtil4 = HttpUtil4Exposer.createHttpClient();
        }
    
        this.xueYuanAccount = xueYuan;

        log.info(this.xueYuanAccount.getUserName()+" init thread");
 
	}
	
	@Override
	public Integer call()  throws Exception {

           int result =0;

		 try {

             log.info(this.xueYuanAccount.getUserName()+" yueche thread start!");

             int loginResult = doLogin();

             if ( loginResult == 0 ){
                 System.out.println(xueYuanAccount.getUserName());

                 for (YueCheItem yueCheItem : xueYuanAccount.getYueCheItemList()){
                     doYuche(yueCheItem);
                     if (yueCheItem.isBookSuccess()){
                         result =0;
                     }
                 }
             } else if ( loginResult == 3 ) {
                 String info ="accountError:"+ xueYuanAccount.getUserName()+","+ xueYuanAccount.getPassword();
                 log.error(info);
                 for (YueCheItem yueCheItem : xueYuanAccount.getYueCheItemList()){
                     YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_ACCOUNT_ERROR, info);
                 }
             }

        } catch (InterruptedException e) {
              log.error("cancel task! ");
               result =2;
        }
        return result;

	}

	/**
	 * 
	 * 扫描程序，进行扫描操作
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scan () throws InterruptedException{
		if (isYueKao())
			return scanYueKao();
		else{
			return scanYueChe();
		}
        		
	}

	private boolean isYueKao(){
		if ( yueCheItem.getKm() != null &&  yueCheItem.getKm().startsWith("ks"))
			return true;
		return false;
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scanYueChe() throws InterruptedException{
		
		doLogin () ;
		System.out.println(yueCheItem.getUserName());
		Result<String> result = canYueChe(yueCheItem.getYueCheDate());
		
		int yueCheInfo = result.getRet();
		
		if (yueCheInfo == 0){
			doYueche(result.getData(), Constants.AM_STR);
		} else if (yueCheInfo == 1){
			doYueche(result.getData(),Constants.PM_STR);
			
		}else if (yueCheInfo == 2){
			doYueche(result.getData(),Constants.NI_STR);
		}else if (yueCheInfo == 3){
			return ALREADY_YUECHE;
			
		}else if (yueCheInfo == 4){
		
		}else if (yueCheInfo == 5){
			isLogon =false;
		}else if (yueCheInfo == 6){
			log.info("canYueChe InternalServerError");
		}
		
		if (yueCheItem.isBookSuccess()){
			return SCAN_YUECHE_SUCCESS;
		}
		return NO_CAR;
	}
	
	
	/**
	 * 科目考试
	 * @throws InterruptedException 
	 * 
	 * 
	 */
	public int scanYueKao() throws InterruptedException{
		
		doLogin () ;
		System.out.println(yueCheItem.getUserName());
		
		Result<String> result = canYueKao(yueCheItem.getYueCheDate(), yueCheItem.getYueCheAmPm(), yueCheItem.getKm());
		
		int yueCheInfo = result.getRet();
		if (yueCheInfo == 0){
			doYueKao( yueCheItem.getKm());
			
		} else if (yueCheInfo == 1){
			
		}else if (yueCheInfo == 2){
			return ALREADY_YUECHE;
		}else if (yueCheInfo == 3){
			return ALREADY_YUECHE;
			
		}else if (yueCheInfo == 4){
		
		}
		
		if (yueCheItem.isBookSuccess()){
			return SCAN_YUECHE_SUCCESS;
		}
		return NO_CAR;
	}


    /**
     *
     *0 登录成功
     *1 登录失败
     *2 已经约车成功
     *3 账号密码错误
     *3 无法进行下一步了
     * */
	private  int  doLogin () throws InterruptedException {
		
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
	          int loginResult =  login(xueYuanAccount.getUserName() , xueYuanAccount.getPassword());
	          if (loginResult == YueChe.LONGIN_SUCCESS){
	              isLoginSuccess =  true;
	          } else if( loginResult == YueChe.LONGIN_ACCOUNT_ERROR ){

                  return 3;
              }
	           
	       }while (!isLoginSuccess);
	        
	        isLogon = true;
	        lastLoginTime = System.currentTimeMillis();
	        log.info(xueYuanAccount.getUserName()+" login success!");
		}else{
	        log.info(xueYuanAccount.getUserName()+" retain login status!");

		}
        return 0;
        
    }
	
	
    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYueche ( String date, String amPm ) throws InterruptedException {
    
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
        
             Result<String> ret =  null;
             if(Constants.KM3.equals(yueCheItem.getKm())){
            	 ret =  yuche(date, amPm,Constants.KM3_HiddenKM, yueCheItem.getPhoneNum());
             }else if (Constants.KM1.equals(yueCheItem.getKm())){
            	 ret =  yuche(date, amPm,Constants.KM1_HiddenKM, yueCheItem.getPhoneNum());
             }else if (Constants.KM_AUTO.equals(yueCheItem.getKm())) {
            	 ret =  yuche(date, amPm,0, yueCheItem.getPhoneNum());
             }else{
            	 ret =  yuche(date, amPm,Constants.KM2_HiddenKM, yueCheItem.getPhoneNum());
             }

             int  result  = ret.getRet();
             
          if (result == YueChe.BOOK_CAR_SUCCESS){
              isSuccess = true;
              String info = yueCheItem.getUserName() +":"+ret.getData()+":"+date +"约车成功";
              System.out.println(info);
              log.info(info);
              YueCheHelper.updateYueCheBookInfo(yueCheItem.getUserName(),date, YueCheItem.BOOK_CAR_SUCCESS, info);

              yueCheItem.setBookSuccess(isSuccess);
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

    
    /**
     * @throws IOException 
     * 
     */
    private  void  doYueKao (String ks) throws InterruptedException {
    
    	//按情况约车
    
    	boolean  isSuccess = false;
        boolean first = true;
        do {
             if (!first){
                 log.error("yuche  error. retry!");
                 Thread.sleep(1000 * RandomUtil.getRandomInt(YueCheHelper.MAX_SLEEP_TIME));
             }else{
                 first = false;
             }
        
          Result<String > ret =  yueKao(ks);
          
          int  result  = ret.getRet();
             
          if (result == YueChe.YUE_KAO_SUCCESS){
              isSuccess = true;
              String info = yueCheItem.getUserName() +":"+ret.getData() +"约考成功";
              System.out.println(info);
              log.info(info);
              yueCheItem.setBookSuccess(isSuccess);
          }else if (result == YueChe.YUE_KAO_NO_POSITION){  
              System.out.println(date + "可预约人数不足");
              break;
          }else if (result == YueChe.YUE_KAO_CANCEL){   
              System.out.println("错误！取消！错误");
          }else if (result == YueChe.YUE_KAO_ERROR){   
              System.out.println("约考失败");
              break;
          }else {  //无车
              System.out.println("未知错误！重试!RUSULT="+result);
          }
          
         }while (!isSuccess);
        log.info("yuekao finish !");
        return ;
    }



    /**
     * @throws IOException
     *
     */
    private  void  doYuche (YueCheItem yueCheItem) throws InterruptedException {
        //am,pm,ni  to 812,15,58
        String shiDuan = YueCheHelper.transformYueCheShiDuanToNum(yueCheItem.getYueCheAmPm());

        int count =0;

        boolean  isSuccess = false;        //按情况约车
        boolean first = true;

        do {
            count ++; //十次就测试
            if(count % 10 == 0){
                int ycResult =  YueCheHelper.getYueCheBookInfo(yueCheItem.getId());
                if(ycResult == YueCheItem.BOOK_CAR_SUCCESS ||
                        ycResult == YueCheItem.BOOK_CAR_ALREADY_BOOKED_CAR ){
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
            if(Constants.KM3.equals(yueCheItem.getKm())){
                ret =  yuche(yueCheItem.getYueCheDate(), shiDuan,Constants.KM3_HiddenKM, yueCheItem.getPhoneNum());
            }else if (Constants.KM1.equals(yueCheItem.getKm())){
                ret =  yuche(yueCheItem.getYueCheDate(), shiDuan,Constants.KM1_HiddenKM, yueCheItem.getPhoneNum());
            }else if (Constants.KM_AUTO.equals(yueCheItem.getKm())) {
                ret =  yuche(yueCheItem.getYueCheDate(), shiDuan,0, yueCheItem.getPhoneNum());
            }else{
                ret =  yuche(yueCheItem.getYueCheDate(), shiDuan,Constants.KM2_HiddenKM, yueCheItem.getPhoneNum());
            }

            String uinfo = yueCheItem.getUserName() +":"+yueCheItem.getYueCheDate()+ YueCheHelper.AMPM.get(shiDuan);

            int  result  = ret.getRet();

            if (result == YueChe.BOOK_CAR_SUCCESS){
                isSuccess = true;
                uinfo = yueCheItem.getUserName() +":"+ret.getData()+":"+yueCheItem.getYueCheDate()+ YueCheHelper.AMPM.get(shiDuan);
                String info =uinfo +"约车成功！";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_SUCCESS, info);
                yueCheItem.setBookSuccess(isSuccess);
                break;
            }else if (result == YueChe.ALREADY_BOOKED_CAR){  //该日已经预约车辆
                String info = uinfo+ "该日已经预约车辆了！";
                log.info(info);
                System.out.println(info);
                isSuccess = true;
                yueCheItem.setBookSuccess(isSuccess);
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_ALREADY_BOOKED_CAR, info);
                break;
            }else if (result == YueChe. KEMU2_NO_TIME){  //无车
                String info = uinfo +"科目二剩余小时不足!";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_KEMU2_NO_TIME, info);
                return ;
            } else if (result == YueChe. PHONE_NUM_ERROR){  //对不起,您填写的报名时预留的手机或固定电话号码不正确
                String info = uinfo +"对不起,您填写的报名时预留的手机或固定电话号码不正确";
                System.out.println(info);
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_PHONE_NUM_ERROR, info);
                return ;
            }

            else if (result == YueChe.NO_CAR){  //无车的话，赶紧约下班车
                if (YueCheHelper.isInQiang15ServiceTime() || YueCheHelper.isInQiang14ServiceTime() ){
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
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_NOT_BOOK_WEEKEND_CAR, info);
                return;
            } else if (result == YueChe.CAR_TYPE_ERROR){  //您不能预约该车型的车
                String info = uinfo +"您不能预约该车型的车!";
                log.info(info);
                YueCheHelper.updateYueCheBookInfo(yueCheItem.getId(), YueCheItem.BOOK_CAR_CAR_TYPE_ERROR, info);
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
