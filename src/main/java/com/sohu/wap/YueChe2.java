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
public class YueChe2 {

	private static Logger log = LoggerFactory.getLogger(YueChe2.class);

	public static String LOGIN_URL = "http://haijia.bjxueche.net/";
	private static String LOGOUT_URL = "http://haijia.bjxueche.net/Login.aspx?LoginOut=true";

	public static String LOGIN_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=ImgCode&random=";

	private static String YUCHE_URL = "http://haijia.bjxueche.net/ych2.aspx";
	private static String YUEKAO_URL = "http://haijia.bjxueche.net/yk2.aspx";
	
	private static String GET_CARS_URL = "http://haijia.bjxueche.net/Han/ServiceBooking.asmx/GetCars";
	protected static String BOOKING_CAR_URL = "http://haijia.bjxueche.net/Han/ServiceBooking.asmx/BookingCar";
	public  static String BOOKING_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=BookingCode&random=";

	private static String __VIEWSTATE = "__VIEWSTATE";
	private static String __EVENTVALIDATION = "__EVENTVALIDATION";
	
	private static String __EVENTARGUMENT = "__EVENTARGUMENT";
    private static String __EVENTTARGET = "__EVENTTARGET";
	
	
	private static String HIDDEN_KM = "hiddenKM";

	
	
	public static int YUCHE_RETRY_TIME = 3;

	
	protected  HttpUtil4 httpUtil4 = HttpUtil4.getInstanceHaveCookie(); //默认值
	
	
	//login 初始页面设置
	private long visitLoginUrlTime  = 0;
	private boolean isVisitedLoginUrl = false;
	private Element viewState;
	private Element eventValid;
	
	private Element eventTarget;
    private Element eventArgument;
    
	
	
	private Map<String, DayCarInfo> yueCheCarInfoMap = new HashMap<String, DayCarInfo>();
	
	private Map<String, DayKaoShiInfo> kaoShiInfoMap = new HashMap<String, DayKaoShiInfo>();
	
	/**
	 * 
	 * 处理用户登录
	 * @throws InterruptedException 
	 * 
	 */
	public  boolean login(String userName, String passwd) {
		long currentTime = System.currentTimeMillis();
		
		//为了加快访问速度，只加载一次login页面
		//如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
		if (  !isVisitedLoginUrl || (currentTime - visitLoginUrlTime > YueCheHelper.LOGIN_SESSION_TIMEOUT_MILLISECOND) ){
			String firstPage = httpUtil4.getContent(LOGIN_URL);
			if (firstPage == null || firstPage.length()< 100) { //失败，一切不操作
				return false;
			}
			Document document = Jsoup.parse(firstPage);
			viewState = document.getElementById(__VIEWSTATE);
			eventValid = document.getElementById(__EVENTVALIDATION);
			isVisitedLoginUrl = true;
			visitLoginUrlTime = System.currentTimeMillis();
		}
		
		
		
		boolean isLoginSuccess = false;
		do {
			//模拟用户行为，一直请求验证码
			String imageCode = null;
			do{
				try {
					imageCode = getImgCode(LOGIN_IMG_URL);
				} catch (IOException e1) {
					log.error("get image code error", e1);
				}
			}while(imageCode == null);
			
			
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
			
			//进入creak 模式
			 if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
			    
			    ( (HttpUtil4Exposer)httpUtil4).addCookie(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY, 
			             CookieImgCodeHelper.getImageCodeCookie().get(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY).getCookie());
			 }
			
			do{
				 result = httpUtil4.post(LOGIN_URL, json);
				 if (result == null){
//					 ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
				 }else{
					 break;
				 }
			
			}while(result == null);
			
		
			if (result != null) {
//				System.out.println(result);
//				log.debug(result);
				//登录成功
				if (result.equals("/index.aspx")) {
					isLoginSuccess =true;
				} else if(result.indexOf("验证码错误")!= -1 ||result.indexOf("请输入验证码")!= -1 ){  //失败的话 ，继续登录
					System.out.println("验证码识别错误！登录失败.");
				}else if(result.indexOf("账号或密码错误")!= -1  ){  //失败的话 ，继续登录
					System.out.println("账号或密码错误！登录失败.");
					log.error("账号或密码错误"); //打印错误，直接退出
					log.error("accountError:"+userName+","+passwd); //打印错误，直接退出
					ThreadUtil.sleep (YueCheHelper.MIN_SCAN_INTEVAL +RandomUtil.getRandomInt(YueCheHelper.MAX_SCAN_INTEVAL-YueCheHelper.MIN_SCAN_INTEVAL));
				}else if (result.indexOf("系统服务时间每天从07:35-20:00")!= -1  ){
					System.out.println("系统服务时间每天从07:35-20:00;"+"enter sleep");
					ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
//					ThreadUtil.sleep (YueCheHelper.MIN_SCAN_INTEVAL +RandomUtil.getRandomInt(YueCheHelper.MAX_SCAN_INTEVAL-YueCheHelper.MIN_SCAN_INTEVAL));
				}else if (result.indexOf("桑塔纳、富康车型学员登陆时间为每天 07:40 以后!")!= -1  ){
					System.out.println("桑塔纳、富康车型学员登陆时间为每天 07:40 以后!;"+"enter sleep");
					ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
//					ThreadUtil.sleep (YueCheHelper.MIN_SCAN_INTEVAL +RandomUtil.getRandomInt(YueCheHelper.MAX_SCAN_INTEVAL-YueCheHelper.MIN_SCAN_INTEVAL));
				}else if(result.indexOf("您的IP地址被限制登录!")!= -1  ){  //失败的话 ，继续登录
					System.out.println("您的IP地址被限制登录!");
					log.error("您的IP地址被限制登录!"); //打印错误，直接退出
					System.exit(1);
				}
				
				
				else{
					log.debug(result);
					System.out.println(result);
				}
//				
			 
			}
		}while(!isLoginSuccess);
	
		return true;
	}

	String getHiddenKM(boolean isGetHiddenKM){
		// 页面中一个隐藏的输入，默认为2，可能更改
		String hiddenKM = "2";
		if (isGetHiddenKM) {
			String yuchePage = httpUtil4.getContent(YUCHE_URL);
			Document document = Jsoup.parse(yuchePage);
			Element hkm = document.getElementById(HIDDEN_KM);
			hiddenKM = hkm.attr("value");
		}
		return hiddenKM;
	}
	
	String getImgCode(){
		
		String imageCode = null;
		
		do{
			try {
				imageCode = getImgCode(BOOKING_IMG_URL);
				} catch (IOException e1) {
					log.error("get book image code error", e1);
					imageCode =null;
				}
		}while(imageCode == null);
		return imageCode;
	}
	
	
	
	/**
	 * -1 约车错误 0 约车成功 1 无车可约
	 * 
	 */
	public  Result<String> yuche(String date, String amOrpm, boolean isGetHiddenKM)
			throws InterruptedException {
	    
	    Result <String>result = new Result<String>(BookCarUtil.UNKNOWN_ERROR);
	    
		int resultN = BookCarUtil.UNKNOWN_ERROR;

		// 页面中一个隐藏的输入，默认为2，可能更改
		String hiddenKM = getHiddenKM(isGetHiddenKM);
	
		// {"yyrq":"20121126","yysd":"58","xllxID":"2","pageSize":35,"pageNum":1}
		JSONObject json = new JSONObject();
		try {
			json.put("yyrq", date);
			json.put("yysd", amOrpm);
			json.put("xllxID", hiddenKM);
			json.put("pageSize", 35);
			json.put("pageNum", 1);
		
			Result<JSONArray> carRet ;
		
			do{
				// 得到某天的信息
				JSONObject carsJson = httpUtil4.postJson(GET_CARS_URL, json);
				carRet = BookCarUtil.carsResult(carsJson);
			
				if (BookCarUtil.GET_CAR_ERROR == carRet.getRet() ) {
					log.error("get car info error");
					ThreadUtil.sleep(1);
				}else{
					break;
				}
			}while(true);
			
			
			JSONArray carsArray = new JSONArray();
			if (carRet.getRet() == BookCarUtil.HAVE_CAR){
				carsArray = carRet.getData();
				
			}else if(carRet.getRet() == BookCarUtil.IP_FORBIDDEN ){
				ThreadUtil.sleep(YueCheHelper.WAITTING_SCAN_INTERVAL);
//				System.exit(1);
				
			} else if (carRet.getRet() == BookCarUtil.NO_CAR ){
				resultN = BookCarUtil.NO_CAR;
				result.setRet(resultN);
				return result;
			}else {
				
			}
		
			JSONObject	selectedCar = null;
			// GetCar over

			// 下一步，约车
			int yucheTry = 0;
			
			
			//如果是 creak 模式
            if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
              ((HttpUtil4Exposer)httpUtil4).addCookie(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY, 
                        CookieImgCodeHelper.getImageCodeCookie().get(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY).getCookie());
            }
            
            JSONObject cookieJson = new JSONObject();
            cookieJson.put(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY, ((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY));
            cookieJson.put(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY, ((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY));
            cookieJson.put(CookieImgCodeHelper.COOKIE_LOGINON_KEY, ((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_LOGINON_KEY));
            cookieJson.put(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY, ((HttpUtil4Exposer)httpUtil4).getCookieValue(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY));
        

			do {
				
				if (YueCheHelper.IS_USE_PROXY_BOOK_CAR){
					BookCar.book(carsArray, cookieJson);
					
				}else{
					selectedCar = carsArray.getJSONObject(RandomUtil.getRandomInt(carsArray.length()));
					if (selectedCar != null) {
						log.info("选择的车是：" + selectedCar.toString());
						System.out.println("选择的车是：" + selectedCar.toString());
						
						String imageCode = getImgCode();
						JSONObject bookCarJson = getBookCarJson(selectedCar, imageCode ,hiddenKM);
						
//						ThreadUtil.sleep(1);
			             
			            int intResult = 0; 
						
						do{
							JSONObject bookResult = httpUtil4.postJson(BOOKING_CAR_URL, bookCarJson);
							 intResult = BookCarUtil.bookResult(bookResult);
							if (intResult == BookCarUtil.BOOK_CAR_TIMEOUT) {
								System.out.println("book car timeout or error");
								log.error("book car timeout or error");
								ThreadUtil.sleep(1);
							}
						}while(intResult == BookCarUtil.BOOK_CAR_TIMEOUT);
						
						yucheTry++;
					
						if (BookCarUtil.BOOK_CAR_SUCCESS == intResult) {
							System.out.println("预约成功!...");
							String info = "Info:"+selectedCar.getString("YYRQ") + ":"
									+ selectedCar.getString("XNSD") + "-"
									+ selectedCar.getString("CNBH");
							result.setData(info);
							System.out.println(info);
							log.info(info);
							resultN =BookCarUtil.BOOK_CAR_SUCCESS;
						
						} else {
							
							if (BookCarUtil.TODAY_ALREADY_BOOKED_CAR == intResult 
									|| BookCarUtil.BOOK_INVAILD_OPERATION == intResult ){
								break;
							} else if ( BookCarUtil.BOOK_CAR_IMAGE_CODE_ERROR == intResult){
								System.out.println("验证码错误！不计入retry次数");
							} else if(BookCarUtil.CAR_BOOKED  == intResult){
								yucheTry++;  //该页面可能都被约了
							} else {
								log.error("book car return error:"+intResult);
							}
					 }

					}
				}
					
			} while (resultN != BookCarUtil.BOOK_CAR_SUCCESS && yucheTry < YUCHE_RETRY_TIME);
		} catch (JSONException e) {
			log.error("error,", e);
			e.printStackTrace();
		}
	    result.setRet(resultN);
		return result;
	}

	public JSONObject getBookCarJson (JSONObject selectedCar , String imageCode,  String hiddenKM ){
	
		String md5Code = MD5.crypt(imageCode.toUpperCase());
		// {"yyrq":"20121126","xnsd":"58","cnbh":"06204","imgCode":"d32926ad20c3ef9b703472edba4d413d","KMID":"2"}
		JSONObject bookCarJson = new JSONObject();
		try {
			bookCarJson.put("yyrq", selectedCar.getString("YYRQ"));
			bookCarJson.put("xnsd", selectedCar.getString("XNSD"));
			bookCarJson.put("cnbh", selectedCar.getString("CNBH"));
			bookCarJson.put("imgCode", md5Code);
			bookCarJson.put("KMID", hiddenKM);

		} catch (JSONException e) {
			log.error("error",e);
		}
		return bookCarJson;
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
		String fileName = Util.generateUUID() + ".gif"; // 生成唯一的id
		
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
	    if (YueCheHelper.IS_ENTER_CREAKER_MODEL){
	        if (url.equals(LOGIN_IMG_URL)){
	            VerifyCode  vcode=  CookieImgCodeHelper.getImageCodeCookie().get(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY);
	            return vcode.getVcode();
	        }else{
	            VerifyCode  vcode=  CookieImgCodeHelper.getImageCodeCookie().get(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY);
                return vcode.getVcode();
	        }
	       
	    }else{
	        if (YueCheHelper.IMAGE_CODE_INPUT_METHOD_IS_AUTO){
	            return getImgCodeAuto(url);
	        }else{
	            return  getImgCodeManual(url);
	        }
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
			throw new IOException("download image error!");
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
			imageCode =imageCode.replaceAll("[^0-9a-zA-Z]", "");
			
			System.out.println("自动识别结果:" + imageCode + "; \r\n");
		
			if(imageCode.length() != 4 ){
				throw new IOException("scan image code error!");
			}
		} catch (Exception ex){
			log.error("tessecret return null", ex);
		}
//		IO.deleteFile(storeAddress);

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
			
			carInfo.getCarInfo().put(YueCheHelper.AM_STR, amStatus);
			carInfo.getCarInfo().put(YueCheHelper.PM_STR, pmStatus);
			carInfo.getCarInfo().put(YueCheHelper.NI_STR, niStatus);
			
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
	 */
	public Result<String>  canYueChe (String yueCheDateArray,  String amPm){
		
	    Result<String> ret = new Result<String>(4);
	    
		String result = getAvailableCarInfo();
		
		if (result.equals("noLogin")){
		    ret.setRet(5);
			return ret;
		}else{
		    String[] array =  yueCheDateArray.split("[,]");
		    for(String yueCheDate : array){
		          DayCarInfo ycCarInfo =  yueCheCarInfoMap.get(yueCheDate);
		            if (ycCarInfo != null){
		                String[] timeArray = amPm.split("[,;]");
		                if (timeArray.length  <  0) {
		                    timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
		                }
		                boolean havaCar = false;
		                for (String amPmStr : timeArray){  //按情况约车
		                     String info = ycCarInfo.getCarInfo().get(amPmStr);
		                     if (info.equals("无")){
		                         
		                    }else if (info.equals("已约")){
		                        ret.setRet(3);
		                        return ret;
		                    }else{
		                        ret.setData(yueCheDate); //设置约车日期
		                        if (YueCheHelper.AM_STR.equals(amPmStr)){
		                            ret.setRet(0);
		                           
		                            return ret;
		                        
		                        }else if (YueCheHelper.PM_STR.equals(amPmStr)){
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

	
	
//扫描table ，得到约车信息
    
    private   String getAvailableYueKaoInfo(){
        String ykPage = null;
        do{
             ykPage = httpUtil4.getContent(YUEKAO_URL);
             
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
            int index = href.indexOf("&#39;");
           
            String kaoshi =href.substring( index ,href.indexOf("&#39;", index+5));
            DayKaoShiInfo carInfo = new DayKaoShiInfo();
            carInfo.setDate(date.replace("-", ""));
            carInfo.setAmPm(amPm);
            carInfo.setRemindNum(remindNum) ;
            
            carInfo.setKaoShi(kaoshi);
            
            kaoShiInfoMap.put(date,carInfo);
            
        }
        return "getedYueKaoInfo";
    }
	
	
	Result<String > yueKao (String date){
	    return null;
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
