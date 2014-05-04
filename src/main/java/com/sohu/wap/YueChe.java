/**
 *@version:2012-11-19-上午10:39:15
 *@author:jianjunwei
 *@date:上午10:39:15
 *
 */
package com.sohu.wap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.DayCarInfo;
import com.sohu.wap.bo.DayKaoShiInfo;
import com.sohu.wap.bo.Result;
import com.sohu.wap.bo.VerifyCode;
import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.util.IO;
import com.sohu.wap.util.MD5;
import com.sohu.wap.util.MyImgFilter;
import com.sohu.wap.util.OSUtil;
import com.sohu.wap.util.RandomUtil;
import com.sohu.wap.util.SystemConfigurations;
import com.sohu.wap.util.ThreadUtil;
import com.sohu.wap.util.Util;

/**
 * @author jianjunwei
 * 
 */
public class YueChe {

	private static Logger log = LoggerFactory.getLogger(YueChe.class);

	public static String LOGIN_URL = "http://106.37.230.254:81/";
	private static String LOGOUT_URL = "http://haijia.bjxueche.net/Login.aspx?LoginOut=true";
	private static String TOP_URL =  "http://haijia.bjxueche.net/top.aspx";
	public static String LOGIN_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=ImgCode&random=";

	private static String YUCHE_URL = "http://106.37.230.254:81/ych2.aspx";
	
	private static String YUEKAO2_URL = "http://haijia.bjxueche.net/yk2.aspx";
	private static String YUEKAO21_URL = "http://haijia.bjxueche.net/yk21.aspx";
	private static String YUEKAO3_URL = "http://haijia.bjxueche.net/yk3.aspx";
	private static String YUEKAO4_URL = "http://haijia.bjxueche.net/yk4.aspx";

	private static String GET_CARS_URL = "http://106.37.230.254:81/Tools/km2.aspx";
	protected static String BOOKING_CAR_URL = "http://106.37.230.254:81/Tools/km2.aspx";
	public  static String BOOKING_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=BookingCode&random=";
	public  static String BOOKING2_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode2.ashx?key=ImgCode&random=";

	private static String __VIEWSTATE = "__VIEWSTATE";
	private static String __EVENTVALIDATION = "__EVENTVALIDATION";
	
	private static String __EVENTARGUMENT = "__EVENTARGUMENT";
    private static String __EVENTTARGET = "__EVENTTARGET";
	
    private static String COOKIE_IMG_CODE_KEY="ImgCode";
    
	private static String HIDDEN_KM = "RdoType_1";

	public static int UNKNOWN_ERROR = -1;
	public static int BOOK_CAR_SUCCESS = 0;
	public static int NO_CAR = 1;
	public static int GET_CAR_ERROR = 2;
	public static int GET_BOOK_CODE_ERROR = 123323;
	public static int ALREADY_BOOKED_CAR=3;
	public static int BOOK_INVAILD_OPERATION = 5;
	public static int CAR_TYPE_ERROR = 6;
	public static int YUE_KAO_SUCCESS = 0;
	
	public static int YUE_KAO_ERROR = 1;

	public static int YUE_KAO_CANCEL = 2;
	
	public static int ALREADY_YUE_KAO = 3;
	
	public static int YUE_KAO_NO_POSITION = 4;
	public static int PHONE_NUM_ERROR=800006;
	public static int NOT_BOOK_WEEKEND_CAR = 800005;
	public static int KEMU2_NO_TIME=10003;
	public static int IP_FORBIDDEN = 1000333;
	/**
	 * 
	 *
	 * 处理用户登录
	 * @return
	 * 0 成功
	 * 1 失败
	 * 2 代理服务器失效
	 * 3 帐号密码错误
	 *  
	 */
	public static int LONGIN_SUCCESS =0;
	public static int LONGIN_ERROR =1;
	public static int LONGIN_PROXY_ERROR =2;
	public static int LONGIN_ACCOUNT_ERROR =3;
	public static int LONGIN_IP_FORBIDDEN =4;
//	public static int LONGIN_ERROR =1;
	
	private String keepImgCode=null;
	
	public static int YUCHE_RETRY_TIME = 5;

	
	protected  HttpUtil4 httpUtil4 = HttpUtil4.getInstanceHaveCookie(); //默认值
	
	
	//login 初始页面设置
	private long visitLoginUrlTime  = 0;
	private boolean isVisitedLoginUrl = false;
	private Element viewState;
	private Element eventValid;
	
	private Element eventTarget;
    private Element eventArgument;
    
    private String eventTargetString;
    
	private String yueKaoInfoDetail;
	
	private Map<String, DayCarInfo> yueCheCarInfoMap = new HashMap<String, DayCarInfo>();
	
	private Map<String, DayKaoShiInfo> kaoShiInfoMap = new HashMap<String, DayKaoShiInfo>();
	
	/**
	 * 
	 *
	 * 处理用户登录
	 * @return
	 * 0 成功
	 * 1 失败
	 * 2 代理服务器失效
	 * 3 帐号密码错误
	 * @throws InterruptedException 
	 *  
	 */
	public  int  login(String userName, String passwd) throws InterruptedException {
		
		long currentTime = System.currentTimeMillis();
		
		//为了加快访问速度，只加载一次login页面
		//如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
		if (  !isVisitedLoginUrl || (currentTime - visitLoginUrlTime > YueCheHelper.LOGIN_SESSION_TIMEOUT_MILLISECOND) ){
			String firstPage = httpUtil4.getContent(LOGIN_URL);
			if (firstPage == null) {        //失败， 也可能是因代理服务器失效，暂时无法判断
				return LONGIN_ERROR;
			}else if (firstPage.indexOf("Internal Server Error")!=-1){
			    return LONGIN_ERROR;
			}
			else if ( firstPage.length()< 100 ||  firstPage.indexOf("驾驶人计时培训-网络预约平台") ==-1 ){
				return LONGIN_PROXY_ERROR;
			}
			
			Document document = Jsoup.parse(firstPage);
			viewState = document.getElementById(__VIEWSTATE);
			eventValid = document.getElementById(__EVENTVALIDATION);
			isVisitedLoginUrl = true;
			visitLoginUrlTime = System.currentTimeMillis();
		}
		
		//后面可以不考虑代理失败的情况
	
		boolean isLoginSuccess = false;
		boolean isPreemptiveImgCodeCreakSuccess =false;
		do {
			int retry_count =0;
			//模拟用户行为，一直请求验证码
			String imageCode = null;
//            不需要验证码
//
//			if (YueCheHelper.isEnterCreakerModel()){      //进入creak模式,使用已经有的验证码
//		        VerifyCode  vcode=  CookieImgCodeHelper.getImageCodeCookie(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE);
//		        imageCode = vcode.getVcode();
//		       ((HttpUtil4Exposer)httpUtil4).addCookie(COOKIE_IMG_CODE_KEY, vcode.getCookie());
//		       ((HttpUtil4Exposer)httpUtil4).addCookie(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY, vcode.getAspSessionId());
//		    }else if(isPreemptiveImgCodeCreakSuccess){ //如果抢先登录成功了，验证码破解成功
//		    	imageCode=keepImgCode;
//		    }else{
//		    	do{
//					retry_count++;
//					try {
//						imageCode = getImgCode(LOGIN_IMG_URL);
//					} catch (IOException e1) {
//						log.error("get image code error", e1);
//					}
//					if( retry_count > YueCheHelper.NET_RETRY_LIMIT){
//						log.error("login error, get image code count extend retry_limit");
//						return LONGIN_ERROR;
//					}
//				}while(imageCode == null);
//		    }
			JSONObject json = new JSONObject();
			try {
				json.put("txtUserName", userName);
				json.put("txtPassword", passwd);
				json.put("BtnLogin", "登  录");
				if (viewState!= null){
					json.put(__VIEWSTATE, viewState.attr("value"));
				}
				if (eventValid != null){
					json.put(__EVENTVALIDATION, eventValid.attr("value"));;
				}
				json.put("rcode", "");
				json.put("txtIMGCode", imageCode);

			} catch (JSONException e) {
				log.error("error", e);
				e.printStackTrace();
			}
			
			//服务器失败的话，一直重试
			String result = null;
			
			do{
				 result = httpUtil4.post(LOGIN_URL, json);
				 if (result == null){
					 System.out.println("server not response");
//					 ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
				 }else{
					 break;
				 }
			
			}while(result == null);
//	        System.out.println("loginseesionid="+((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY));
//	        String logon =((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_LOGINON_KEY) ;
//	        System.out.println("logon="+logon);
//	        ((HttpUtil4Exposer)httpUtil4).addCookie(CookieImgCodeHelper.COOKIE_LOGINON_KEY, logon);
		
			if (result != null) {
//				System.out.println(result);
				log.debug(result);
				//登录成功
				if (result.equals("/index.aspx")) {
                    keepImgCode = imageCode;
					isLoginSuccess =true;
				} else if(result.indexOf("验证码错误")!= -1 ||result.indexOf("请输入验证码")!= -1 ){  //失败的话 ，继续登录
					System.out.println("验证码识别错误！登录失败.");
				}else if (result.indexOf("系统服务时间每天从07:35-20:00")!= -1  ){
                    keepImgCode = imageCode;
					isPreemptiveImgCodeCreakSuccess =true;
					System.out.println("系统服务时间每天从07:35-20:00;"+"enter sleep");
//					testYueChe();
					ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
				}else if (result.indexOf("桑塔纳、富康车型学员登陆时间为每天 07:40 以后!")!= -1  ){
					System.out.println("桑塔纳、富康车型学员登陆时间为每天 07:40 以后!;"+"enter sleep");
					ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
				}else if(result.indexOf("您的IP地址被限制登录!")!= -1  ){  //失败的话 ，继续登录
					System.out.println("您的IP地址被限制登录!");
					log.error("您的IP地址被限制登录!"); //打印错误，直接退出
					return LONGIN_IP_FORBIDDEN;
				}else if(result.indexOf("账号或密码错误")!= -1  ){  //失败的话 ，继续登录
					System.out.println("账号或密码错误！登录失败.");
					log.error("账号或密码错误"); //打印错误，直接退出
					return LONGIN_ACCOUNT_ERROR;
				}
				else{
					log.debug(result);
					System.out.println(result);
				}
//				
			 
			}
		}while(!isLoginSuccess);
	
		return LONGIN_SUCCESS;
	}

	private void printXuanYuanInfo(){
		String info = httpUtil4.getContent(TOP_URL);
		System.out.println("printXuanYuanInfoSessionId="+((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY));

		if(info != null){
//			<span id="lblName">魏建军</span>
			int nameStart = info.indexOf("<span id=\"lblName\">");
			if(nameStart!= -1){
				String endName = info.substring(nameStart+ 19);
				int endNameIndex = endName.indexOf("<");
				String name = endName.substring(0, endNameIndex);
				System.out.println(name);

			}
		}else{
			System.out.println("get xueyuan info error");

		}
	}
	
	private void testYueChe(){
		String imageCode ="";
//		if (YueCheHelper.isEnterCreakerModel()){      //进入creak模式,使用已经有的验证码
//	        VerifyCode  vcode=  CookieImgCodeHelper.getImageCodeCookie(VerifyCode.CODE_TYPE_BOOKING_CODE);
//	        imageCode = vcode.getVcode();
//	       ((HttpUtil4Exposer)httpUtil4).addCookie(COOKIE_IMG_CODE_KEY, vcode.getCookie());
//	       ((HttpUtil4Exposer)httpUtil4).addCookie(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY, vcode.getAspSessionId());
//
//	    }
        imageCode= keepImgCode;
		System.out.println("printXuanYuanInfoSessionId="+((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY));
		JSONObject json = new JSONObject();
		try {
			json.put("yyrq", "20140420");
			json.put("yysd", "58");
			json.put("xllxID", "2");
			json.put("pageSize", 35);
			json.put("pageNum", 1);

		} catch (Exception e) {

			e.printStackTrace();
		}
			JSONObject carsJson = null;
//http://haijia.bjxueche.net/Han/ServiceBooking.asmx/GetCars?yyrq=20140420&yysd=58&xllxID=2&pageSize=35&pageNum=1
			 int retry_count =0;
			do{
				// 得到某天的信息
			    retry_count ++;
			    if(retry_count > YueCheHelper.NET_RETRY_LIMIT){
			        log.error("get car info  count extend count");
			        break;
			    }
				carsJson = httpUtil4.postJson(GET_CARS_URL, json);
//				printXuanYuanInfo();
				if (carsJson == null) {
					System.out.println("get car info error");
					log.error("get car info error");
//					ThreadUtil.sleep(1);
				}else{
					break;
				}
			}while(true);
		System.out.println("GET_CARS_URL="+carsJson);
		JSONObject bookCarJson = new JSONObject();
		try {
			bookCarJson.put("yyrq", "20140420");
			bookCarJson.put("xnsd", "812");
			bookCarJson.put("cnbh", "08219");
			bookCarJson.put("imgCode",imageCode.toUpperCase() );
			bookCarJson.put("KMID", "2");

		} catch (Exception e) {

			e.printStackTrace();
		}
		 
		//一直重试，知道返回结果
		JSONObject bookResult = null;
		do{
			bookResult = httpUtil4.postJson(BOOKING_CAR_URL, bookCarJson);
			if (bookResult == null) {
				System.out.println("book car timeout or error");
				log.error("book car timeout or error");
			}
			System.out.println("bookResult="+bookResult);
		}while(bookResult == null);
		
	}
	/**
	 * -1 约车错误 0 约车成功 1 无车可约 ,修正科目信息
	 * @parm km 0 自动选择科目  1-3科目
	 * @param phoneNum 预留的手机号码
	 * 
	 */
	public  Result<String> yuche(String date, String amOrpm, int km, String phoneNum )
			throws InterruptedException {
	    
	    Result <String>result = new Result<String>(UNKNOWN_ERROR);
	    
		int resultN = UNKNOWN_ERROR;

		//判断科目信息
		// 页面中一个隐藏的输入，默认为2，可能更改,其实是科目信息，亲
		String hiddenKM = "3";  //自动选择科目
		if ( km ==0 ) {
			   try{
				     String yuchePage = httpUtil4.getContent(YUCHE_URL);
				     Document document = Jsoup.parse(yuchePage);
				     Element hkm = document.getElementById(HIDDEN_KM);
				     hiddenKM = hkm.attr("value");
				     System.out.println("hiddenKM is "+hiddenKM );
				  }catch(Exception ex){
						        hiddenKM="2";
			 }
		}else {
	        hiddenKM=String.valueOf(km);
		}

//        _	1398664776012
//        date	Mon Apr 28 2014 13:59:36 GMT 0800 (China Standard Time)
//        filters[cnbh]
//        filters[orderby]
//        filters[type]	km2Car
//        filters[xllxid]	3
//        filters[xnsd]	-1
//        filters[yyrq]	20140511
//        orderBy
//        pageno	1
//        pagesize	10
		JSONObject json = new JSONObject();
		try {
            Date now = new Date();
            json.put("_",now.getTime());
            json.put("date",now.toString());
            json.put("filters[cnbh]","");
            json.put("filters[orderby]","");
            json.put("filters[type]","km2Car");
            json.put("filters[xllxid]", hiddenKM);
            json.put("filters[xnsd]", "-1");
            json.put("filters[yyrq]", date);
            json.put("orderBy","");
			json.put("pagesize", 30);
			json.put("pageno", 1);
            String getCarString  =null;
            int retry_count =0;
			do{
				// 得到某天的信息
			    retry_count ++;
			    if(retry_count > YueCheHelper.NET_RETRY_LIMIT){
			        log.error("get car info  count extend count");
			        break;
			    }
                try {
                     getCarString  = httpUtil4.getContent(GET_CARS_URL, json);
                    if (getCarString.equals("null_0")) {
                               //TODO
                    }  else{

                    }
                } catch (IOException e) {
                    log.error("get car info  error",e);
                    getCarString = null;
                }
//				printXuanYuanInfo();
				if (getCarString == null) {
					System.out.println("get car info error");
					log.error("get car info error");
//					ThreadUtil.sleep(1);
				}else{
					break;
				}
			}while(true);
			
//			testYueChe();
			//没有得到车辆信息的话
			if(getCarString == null){
			    result.setRet(GET_CAR_ERROR);
		        return result;
			}

			System.out.println(getCarString);

//            getCarString ="[\n" +
//                    "  {\n" +
//                    "    \"JLCBH\": \"611262\",\n" +
//                    "    \"CNBH\": \"15103\",\n" +
//                    "    \"YT\": null,\n" +
//                    "    \"LXBH\": null,\n" +
//                    "    \"JLYXM\": \"郑宝成\",\n" +
//                    "    \"XNSD\": \"08:00-12:00.812.4,13:00-17:00.15.0,17:00-20:00.58.0,\"\n" +
//                    "  },\n" +
//                    "  {\n" +
//                    "    \"JLCBH\": \"611290\",\n" +
//                    "    \"CNBH\": \"15131\",\n" +
//                    "    \"YT\": null,\n" +
//                    "    \"LXBH\": null,\n" +
//                    "    \"JLYXM\": \"\",\n" +
//                    "    \"XNSD\": \"08:00-12:00.812.0,13:00-17:00.15.4,17:00-20:00.58.0,\"\n" +
//                    "  }\n" +
//                    "]_2";
//

			int splitPosition = getCarString.indexOf("_");
			String carInfo = getCarString.substring(0, splitPosition);
			String nu = getCarString.substring(splitPosition + 1);
			
			int totalNum = Integer.valueOf(nu);
			
			log.info("totalPage:"+totalNum);
            if(totalNum ==0 ){
                resultN = NO_CAR;
                result.setRet(resultN);
                return result;
            }

			JSONArray carsArray = new JSONArray(carInfo);
			System.out.println("可选的车有：" + carsArray.toString());
			log.info("availableCar:" + carsArray.toString());
			if (carsArray.length() == 0) {
				resultN = NO_CAR;
				result.setRet(resultN);
				return result;
			}

			// GetCar over

			// 下一步，约车
			int yucheTry = 0;

            //     车辆信息
            //       {
//                "JLCBH": "611290",
//                    "CNBH": "15131",
//                    "YT": null,
//                    "LXBH": null,
//                    "JLYXM": "",
//                    "XNSD": "08:00-12:00.812.0,13:00-17:00.15.4,17:00-20:00.58.0,"
//            },
			do {
                //选择车辆

                JSONObject selectedCar = null;
                boolean selectedCarSuccess = false;
                String xnsd=null;
                do{
                    selectedCar = carsArray.getJSONObject(RandomUtil.getRandomInt(carsArray.length()));

                    String[]  xnsds =  selectedCar.getString("XNSD").split("[,]");
                    for(int i =0; i< xnsds.length; i++){
                        String xnsd1 = xnsds[i];
                        String[] info = xnsd1.split("[.]");
                        String sdname = info[0];
                        String sdid = info[1];
                        String sl = info[2];
                        if (Integer.parseInt(sl) > 0 &&  ( amOrpm.indexOf(sdid) != -1 )){  //有时段并且符合条件
                            selectedCarSuccess = true;
                            xnsd = sdid;
                            break;
                        }
                    }
                } while (!selectedCarSuccess);

                log.info("选择的车是：" + selectedCar.toString());
                System.out.println("选择的车是：" + selectedCar.toString());


//              http://106.37.230.254:81/Tools/km2.aspx
//              ?jlcbh=611333&yyrqbegin=20140511&xnsd=812&trainType=3&type=km2Car2&_=1398664811123

                JSONObject bookCarJson = new JSONObject();
                try {
                    Date nowBookcar = new Date();
                    bookCarJson.put("_",nowBookcar.getTime());
                    bookCarJson.put("yyrqbegin", date);
                    bookCarJson.put("xnsd", xnsd);
                    bookCarJson.put("jlcbh", selectedCar.getString("JLCBH"));
                    bookCarJson.put("trainType",hiddenKM );
                    bookCarJson.put("type", "km2Car2");

                } catch (Exception e) {
                      e.printStackTrace();
                }
//                ThreadUtil.sleep(1);

                //一直重试，知道返回结果
                String bookResult = null;
                do{
                    try{
                        bookResult = httpUtil4.getContent(BOOKING_CAR_URL, bookCarJson);

                    }catch(IOException ex){

                        bookResult = null;
                    }

                    if (bookResult == null) {
                        System.out.println("book car timeout or error");
                        log.error("book car timeout or error");
                        ThreadUtil.sleep(0.1F);
                    }
                }while(bookResult == null);

                yucheTry++;

                System.out.println(bookResult);

                //错误
                if(bookResult.indexOf("该时段已被别人预约,请刷新页面后重试!") != -1){
							yucheTry++; //该页面可能都被约了
				}
                if ("所在班种不能约周六日车辆".equals(bookResult)){
							resultN = NOT_BOOK_WEEKEND_CAR;
							break;
				}

                if ("您不能预约该时间的车辆!".equals(bookResult)){
                    ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
					yucheTry--; //非预约开放时间，不计入retry次数
                }
				if ("该日已预约过小时".equals(bookResult) || bookResult.indexOf("该日已预约过时段")!=-1 ){
						resultN = ALREADY_BOOKED_CAR;
						break;
				}
                if ("您不能预约该车型的车!".equals(bookResult)){
                    resultN = CAR_TYPE_ERROR;
                    break;
                }
                if(bookResult.indexOf("操作成功")!= -1 ) {
                    System.out.println("预约成功!...");
						String info = ""+date + ":"
								+ xnsd + "-"
								+ selectedCar.getString("JLCBH");
						result.setData(info);
						System.out.println(info);
						log.info(info);
						resultN = BOOK_CAR_SUCCESS;
                }

                log.info(bookResult);


//					JSONArray jbResult = new JSONArray(bookResult.getString("d"));
//
//					// {"d":"[\r\n  {\r\n    \"Result\": true,\r\n    \"OutMSG\": \"\"\r\n  }\r\n]"}
//
//					if (jbResult.getJSONObject(0).getBoolean("Result")) {
//
//
//					} else {
//
//						String outMsg = jbResult.getJSONObject(0).getString("OutMSG");
//						log.info("book car return msg:"+outMsg);

//						if ("非法操作".equals(outMsg)){
//							resultN = BOOK_INVAILD_OPERATION;
//							break;
//						}

//						if("对不起,您填写的报名时预留的手机或固定电话号码不正确！请核对!".equals(outMsg)){
//							resultN = PHONE_NUM_ERROR;
//							break;
//						}
//
//						if(outMsg.indexOf("科目二剩余小时不足") != -1 || outMsg.indexOf("科目二训练小时将会超出小时!") != -1
//							|| outMsg.indexOf("您已经完成了科目二的所有训练！") != -1){
//						      resultN = KEMU2_NO_TIME;
//	                            break;
//	                    }
//						if("非预约开放时间".equals(outMsg)){
//							ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
//							yucheTry--; //非预约开放时间，不计入retry次数
//						}
//						if("验证码错误！".equals(outMsg)){
//							System.out.println(outMsg+"不计入retry次数");
//							 yucheTry--; //验证码错误，不计入retry次数
//						}
//						if(outMsg.indexOf("该车时段已经被约") != -1){
//							yucheTry++; //该页面可能都被约了
//						}
//						//{"d":"[\r\n  {\r\n    \"Result\": false,\r\n    \"OutMSG\": \"科目二剩余小时不足 !\"\r\n  }\r\n]"}
//						log.error("book car return error:"+outMsg);
//					}


            } while (resultN != BOOK_CAR_SUCCESS && yucheTry < YUCHE_RETRY_TIME);
		} catch (JSONException e) {
			log.error("error,", e);
			e.printStackTrace();
		}
	    result.setRet(resultN);
		return result;
	}

	public  boolean logout() {

		String logout = httpUtil4.getContent(LOGOUT_URL);
		return true;
	}
	
	

	/**
	 * 手动输入验证码
	 * 
	 */
	    public   String getImgCodeManual(String url) throws IOException {

		url = url + RandomUtil.getJSRandomDecimals();

		String imageCode = "";
		String fileSuffix =".gif";
		if(url.contains("CreateCode2")){
			 fileSuffix =".jpg";

		}
		String fileName = Util.generateUUID() + fileSuffix; // 生成唯一的id
		
		String imgDir = "d:/haijia/img";
		String comand = "ping	" ; //都有的命令
		if (OSUtil.getOSType() ==OSUtil.LINUX){
			imgDir = SystemConfigurations.getSystemStringProperty("system.img.linux.dir", "/home/wjj/haijia/img");
			comand = "eog	" ;
		}else{
			imgDir = SystemConfigurations.getSystemStringProperty("system.img.dir", "d:/haijia/img");
			comand = "cmd /c start	" ;
		}
		String storeAddress = imgDir  + File.separator + fileName;

		ByteBuffer bytes = null;
		
		// 重试三次
		int loop = 0;
		do {
			bytes = httpUtil4.getImage(url);
			loop++;
		} while (bytes == null && loop < 5);

		if (bytes != null) {
			IO.writeByteToFile(bytes, storeAddress);
		}else{
			throw new IOException("download image error!");
		}

		comand +=  storeAddress; //命令行

		Process process = Runtime.getRuntime().exec(comand);

		int w = 0;
		try {
			w = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("请输入验证码...");

		do {
			BufferedReader strin2 = new BufferedReader(new InputStreamReader(
					System.in));
			imageCode = strin2.readLine().trim();
			System.out.println("输入为:" + imageCode + ";输入错误按R/r,重新下载验证码按D/d,任意键OK");
			String command = strin2.readLine().trim().toLowerCase();
			if (command.equals("r")) {
				System.out.println("重新输入...");
				continue;
			}else if(command.equals("d")){
				System.out.println("重新下载验证码...");
				return null;
			}else{
				break;
			}
			
		} while (true);

		IO.deleteFile(storeAddress);

		return imageCode;
	}
	
	
	private String getImgCode(String url)throws IOException{

        if (YueCheHelper.isImageCodeInputMethodAuto()){
            return getImgCodeAuto(url);
        }else{
            return  getImgCodeManual(url);
        }  
	  
	}
	
	private  String getImgCodeAuto(String url) throws IOException {

		url = url + RandomUtil.getJSRandomDecimals();

		String imageCode = null;
		String  uuid =  Util.generateUUID();
		String fileName = uuid + ".gif"; // 生成唯一的id
		String destName = uuid + ".jpg";
		
		String imgDir = "d:/haijia/img";
		String comand = "ping	" ; //都有的命令
		if (OSUtil.getOSType() ==OSUtil.LINUX){
			imgDir = SystemConfigurations.getSystemStringProperty("system.img.linux.dir", "/home/wjj/haijia/img");
			comand = "eog	" ;
		}else{
			imgDir = SystemConfigurations.getSystemStringProperty("system.img.dir", "d:/haijia/img");
			comand = "cmd /c start	" ;
		}
		String storeAddress = imgDir  + File.separator + fileName;
		String destAddress =  imgDir  + File.separator + destName;
		String textImg =  imgDir  + File.separator + uuid;
		ByteBuffer bytes = null;
		// 重试三次
		int loop = 0;
		do {
			bytes = httpUtil4.getImage(url);
			loop++;
		} while (bytes == null && loop < 5);

		if (bytes != null) {
			IO.writeByteToFile(bytes, storeAddress);
		}else{
			throw new IOException("download image error!");
		}
		try{
			MyImgFilter.transformImg(storeAddress, destAddress);
		}
		catch(Exception e){
			throw new IOException("download image error!",e);
		}
		
		comand +=  storeAddress; //命令行

//		Process process = Runtime.getRuntime().exec(comand);
//
//		int w = 0;
//		try {
//			w = process.waitFor();
//		} catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
		
		if (OSUtil.getOSType() ==OSUtil.LINUX){
			comand =  "tesseract "+ destAddress +" " + textImg ; //命令行
		}else{
			comand =  "tesseract "+ destAddress +" " + textImg ; //命令行
		}
	

		Process process1 = Runtime.getRuntime().exec(comand);

		int w2 = 0;
		try {
			w2 = process1.waitFor();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		System.out.println("请输入验证码\r\n");

		try{
			BufferedReader  strin2 = new BufferedReader(new FileReader(new File(textImg+".txt")));
			imageCode = strin2.readLine().trim();
			//常用识别错误处理
			imageCode=imageCode.replace(")(", "X");
			imageCode=imageCode.replaceAll(" ", "");
			imageCode =imageCode.replaceAll("[^0-9a-zA-Z]", "");
			
			System.out.println("自动识别结果:" + imageCode + "; \r\n");
		
			if(imageCode.length() != 4 ){
				throw new IOException("scan image code error!");
			}
		} catch (Exception ex){
			log.error("tessecret return null", ex);
		}finally{
			try{
				IO.deleteFile(storeAddress);
				IO.deleteFile(destAddress);
				IO.deleteFile(textImg);
			}catch (Exception ex){
				log.error("deleteFile error", ex);
			}
		}
		
		
		return imageCode;
	}
	
	//扫描table ，得到约车信息
	
	private   String getAvailableCarInfo(){
		String yuchePage = null;
		do{
			 yuchePage = httpUtil4.getContent(YUCHE_URL);
			 
		}while(yuchePage == null);
		if (yuchePage.equals("/login.aspx")){
			return "notLogin";
		} else if (yuchePage.equals("Internal Server Error")){
			return "InternalServerError";
		}
	 
		Document document = Jsoup.parse(yuchePage);
		Element table = document.getElementById("tblMain");
		Elements trs = table.getElementsByTag("tr");
		int size  = trs.size();
		for (int i = 1; i < size; i ++){
			Element tr = trs.get(i);
			System.out.println(tr.text());
			Elements tds = tr.getElementsByTag("td");
			String date = tds.get(0).text();
			String amStatus = tds.get(1).text();
			String pmStatus = tds.get(2).text();
			String niStatus = tds.get(3).text();
			
			date =date.replace("-", "");
			DayCarInfo carInfo = new DayCarInfo();
			carInfo.setDate(date);
			carInfo.setAmCarInfo(amStatus);
			carInfo.setPmCarInfo(pmStatus);
			carInfo.setNiCarInfo(niStatus);
			
			carInfo.getCarInfo().put(Constants.AM_STR, amStatus);
			carInfo.getCarInfo().put(Constants.PM_STR, pmStatus);
			carInfo.getCarInfo().put(Constants.NI_STR, niStatus);
			
			yueCheCarInfoMap.put(date,carInfo);
			
		}
		return "getedCarInfo";
	}
	/**
	 * 0 上午可以
	 * 1 下午可以
	 * 2 晚上可以
	 * 
	 * 3 该日已经约车
	 * 4 无车
	 * 5 登录超时
	 * 6 服务器error InternalServerError
	 */
	public Result<String>  canYueChe (String yueCheDateArray ){
		
	    Result<String> ret = new Result<String>(4);
	    
		String result = getAvailableCarInfo();
		if(result.equals("InternalServerError")){
			ret.setRet(6);
			return ret;
		}
		
		if (result.equals("noLogin")){
		    ret.setRet(5);
			return ret;
		}else{
//    		3=330211199308031530;0803;20140418@am,pm|20140419@am,pm;pm;byd;km2
		    String[] array =  yueCheDateArray.split("[|]");
		    for(String oneItem : array){
		    	String[] yueCheDateAndAmPm= oneItem.split("[@]");
		    	String yueCheDate = yueCheDateAndAmPm[0];
		    	String amPm =  yueCheDateAndAmPm[1];
		    	DayCarInfo ycCarInfo =  yueCheCarInfoMap.get(yueCheDate);
	            if (ycCarInfo != null){
	                String[] timeArray = amPm.split("[,;]");
	                if (timeArray.length  <  0) {
	                    timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
	                }
	                //如果今天已经约车了
	                if ( ycCarInfo.getCarInfo().get("am").equals("已约") ||  ycCarInfo.getCarInfo().get("pm").equals("已约") ||  ycCarInfo.getCarInfo().get("ni").equals("已约")){
	                    continue;
	                }
	                boolean havaCar = false;
	                for (String amPmStr : timeArray){  //按情况约车
	                      String info = ycCarInfo.getCarInfo().get(amPmStr);
	                     if (info.equals("无")){
	                         
	                    }else if (info.equals("已约")){
	                        ret.setRet(3);
//	                        return ret;
	                    }else{
	                        ret.setData(yueCheDate); //设置约车日期
	                        if (Constants.AM_STR.equals(amPmStr)){
	                            ret.setRet(0);
	                            return ret;
	                        
	                        }else if (Constants.PM_STR.equals(amPmStr)){
	                           ret.setRet(1);
	                           return ret;
	                        }else{
	                           ret.setRet(2);
	                           return ret;
	                        }
	                    }
	                }
	            }
		          
		    }
		    

		   
		}
		 ret.setRet(4);
         return ret;
	}

	
	
	private String getAvailableYueKaoInfo(String ks){
		if (Constants.KS2.equals(ks)){
			return getAvailableYueKaoInfoBase(YUEKAO2_URL);
		}else if (Constants.KS21.equals(ks)){
			return getAvailableYueKaoInfoBase(YUEKAO21_URL);
		}else if (Constants.KS3.equals(ks)){
			return getAvailableYueKaoInfoBase(YUEKAO3_URL);
		}else if (Constants.KS4.equals(ks)){
			return getAvailableYueKaoInfoBase(YUEKAO4_URL);
		}else{
			return getAvailableYueKaoInfoBase(YUEKAO2_URL);
		}
		
	}

   //扫描table ，得到约车信息
    private   String getAvailableYueKaoInfoBase(String url){
        String ykPage = null;
        do{
             ykPage = httpUtil4.getContent(url);
             
        }while(ykPage == null);
        if (ykPage.equals("/login.aspx")){
            return "notLogin";
        }
     
        Document document = Jsoup.parse(ykPage);
        viewState = document.getElementById(__VIEWSTATE);
        eventValid = document.getElementById(__EVENTVALIDATION);
        
        Element table = document.getElementById("tblMain");
        Elements trs = table.getElementsByTag("tr");
        int size  = trs.size();
        for (int i = 1; i < size; i ++){
            Element tr = trs.get(i);
            System.out.println(tr.text());
            Elements tds = tr.getElementsByTag("td");
            String date = tds.get(0).text();
            String amPm = tds.get(1).text();
            String remindNum = tds.get(2).text();
            Element op =  tds.get(3);
            Element a =op.getElementsByTag("a").get(0);
            String href = a.attr("href");
            String status = a.text();
            int index = href.indexOf("'");
           
            String kaoshi =href.substring( index + 1,href.indexOf("'", index+5));
            DayKaoShiInfo carInfo = new DayKaoShiInfo();
            date = date.replace("-", "");
            carInfo.setDate(date);
            if(amPm.equals("上午")){
            	amPm="am";
     	    }
     	    if (amPm.equalsIgnoreCase("下午")){
     	    	amPm="pm";
     	    }
            carInfo.setAmPm(amPm);
            carInfo.setRemindNum(remindNum) ;
            
            carInfo.setKaoShi(kaoshi);
            carInfo.setStatus(status);
            
            kaoShiInfoMap.put(date+":"+amPm,carInfo);
            
        }
        return "getedYueKaoInfo";
    }
	
    
    /**
	 * 0 可以
	 * 1 不可以
	 * 2 已经约考
	 * 3 其他错误
	 * @param  yueCheDateArray 日期列表
	 * @param  amPm  上下信息
	 * @param  ks    考试信息
	 */
	public Result<String>  canYueKao (String yueCheDateArray,  String amPm ,String ks){
		Result<String> ret = new Result<String>(4);
	  
		String result = getAvailableYueKaoInfo(ks);
	
		if (result.equals("noLogin")){
		    ret.setRet(5);
			return ret;
		}else{
			String[] array =  yueCheDateArray.split("[,]");
		    for(String yueCheDate : array){
		          String[] timeArray = amPm.split("[,;]");
	               if (timeArray.length  <  0) {
	                    timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
	               }
	               for (String amPmStr : timeArray){
	            	   DayKaoShiInfo ykKaoShiInfo  =  kaoShiInfoMap.get(yueCheDate+":"+amPmStr);
	            	   if (ykKaoShiInfo == null){ //没有信息的话
	            		   continue;
	            	   }
	            	    //如果今天已经约车了
	 	                if ( ykKaoShiInfo.getStatus().equals("取消预约") ){
	 	                	ret.setRet(2);
                            return ret;
	 	                }
	 	             //按情况约车
	                    String remindNumStr = ykKaoShiInfo.getRemindNum();
	 	                int remindNum = Integer.valueOf(remindNumStr);
	 	                
	                    if (remindNum <= 0){
	                    	ret.setRet(1);
	                    	return ret;
	                    }else{
	                    	eventTargetString =ykKaoShiInfo.getKaoShi();
	                    	yueKaoInfoDetail = yueCheDate+":"+amPmStr;
	                    	ret.setData(eventTargetString);
	                    	ret.setRet(0);
	                    	System.out.println(yueCheDate+":"+amPmStr+" 可以约考了！");
                            return ret;
	                    }
	                }
	              
		  }
		 
		}
		ret.setRet(4);
		return ret;
		
	
	}
	/**
	 * 
	 * 
	 *约考试情况。。。。 
	 * 
	 * 
	 */
	Result<String > yueKao ( String ks ) throws InterruptedException{
		if (Constants.KS2.equals(ks)){
			return yueKaoCommon(YUEKAO2_URL);
		}else if (Constants.KS21.equals(ks)){
			return yueKaoCommon(YUEKAO21_URL);
//			return getAvailableYueKaoInfoBase(YUEKAO21_URL);
		}else if (Constants.KS3.equals(ks)){
			return yueKaoCommon(YUEKAO3_URL);
//			return getAvailableYueKaoInfoBase(YUEKAO3_URL);
		}else if (Constants.KS4.equals(ks)){
			return yueKaoCommon(YUEKAO4_URL);
//			return getAvailableYueKaoInfoBase(YUEKAO4_URL);
		}else{
			return yueKaoCommon(YUEKAO2_URL);
//			return getAvailableYueKaoInfoBase(YUEKAO2_URL);
		}
		
	}
 
	
	Result<String > yueKaoCommon (String url) throws InterruptedException{
		
		Result <String> ret = new Result<String>(UNKNOWN_ERROR);
		JSONObject json = new JSONObject();
		try {
			json.put(__EVENTTARGET, eventTargetString);
			json.put(__EVENTARGUMENT, "");
			if (viewState!= null){
				json.put(__VIEWSTATE, viewState.attr("value"));
			}
			if (eventValid != null){
				json.put(__EVENTVALIDATION, eventValid.attr("value"));;
			}
		} catch (JSONException e) {
			log.error("error", e);
			e.printStackTrace();
		}
		
		//服务器失败的话，一直重试
		String result = null;
		do{
			 result = httpUtil4.post(url, json);
			 if (result == null){
//				 ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
			 }else{
				 break;
			 }
		
		}while(result == null);
		
		if (result != null) {
//			System.out.println(result);
//			log.debug(result);
//			public static int YUE_KAO_SUCCESS = 0;
//			
//			public static int YUE_KAO_ERROR = 1;
//
//			public static int YUE_KAO_CANCEL = 2;
//			
//			public static int ALREADY_YUE_KAO = 3;
			
			 if (result.indexOf("预约成功！")!= -1 ){
					System.out.println("预约成功！");
					ret.setData(yueKaoInfoDetail);
					ret.setRet(YUE_KAO_SUCCESS);
			}else
			if(result.indexOf("桩考可预约人数不足")!= -1  ){  //失败的话 ，继续登录
				System.out.println("桩考可预约人数不足");
				ret.setRet(YUE_KAO_NO_POSITION);
				
			}else if (result.indexOf("系统服务时间每天从07:35-20:00")!= -1  ){
				System.out.println("系统服务时间每天从07:35-20:00;"+"enter sleep");
				ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
			}else if (result.indexOf("您需要先通过科目二的所有考试，才能预约科目三考试！")!= -1  ){
				System.out.println("您需要先通过科目二的所有考试，才能预约科目三考试！");
				ret.setRet(YUE_KAO_ERROR);
			} else if (result.indexOf("取消成功！")!= -1){ //忙中出错了，取消了
				ret.setRet(YUE_KAO_CANCEL);
			}
			
			else{
				ret.setRet(YUE_KAO_ERROR);
				log.debug(result);
				System.out.println(result);
			}
//			
		 
		}
		
		return ret;
	}
    /**
     * @return the httpUtil4
     */
    public HttpUtil4 getHttpUtil4() {
        return httpUtil4;
    }

    /**
     * @param httpUtil4 the httpUtil4 to set
     */
    public void setHttpUtil4(HttpUtil4 httpUtil4) {
        this.httpUtil4 = httpUtil4;
    }
	
}
