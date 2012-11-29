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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.http.HttpUtil4;
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

	private static String LOGIN_URL = "http://haijia.bjxueche.net/";
	private static String LOGOUT_URL = "http://haijia.bjxueche.net/Login.aspx?LoginOut=true";

	private static String LOGIN_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=ImgCode&random=";

	private static String YUCHE_URL = "http://haijia.bjxueche.net/ych2.aspx";

	private static String GET_CARS_URL = "http://haijia.bjxueche.net/Han/ServiceBooking.asmx/GetCars";
	private static String BOOKING_CAR_URL = "http://haijia.bjxueche.net/Han/ServiceBooking.asmx/BookingCar";
	private static String BOOKING_IMG_URL = "http://haijia.bjxueche.net/tools/CreateCode.ashx?key=BookingCode&random=";

	private static String __VIEWSTATE = "__VIEWSTATE";
	private static String __EVENTVALIDATION = "__EVENTVALIDATION";

	private static String HIDDEN_KM = "hiddenKM";

	public static int UNKNOWN_ERROR = -1;
	public static int BOOK_CAR_SUCCESS = 0;
	public static int NO_CAR = 1;
	public static int GET_CAR_ERROR = 2;
	public static int ALREADY_BOOKED_CAR=3;
	
	public static int YUCHE_RETRY_TIME = 3;

	
	protected  HttpUtil4 httpUtil4 = HttpUtil4.getInstanceHaveCookie(); //默认值
	
	
	//login 初始页面设置
	private long visitLoginUrlTime  = 0;
	private boolean isVisitedLoginUrl = false;
	private Element viewState;
	private Element eventValid;
	
	
	
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
		if ( !isVisitedLoginUrl || (currentTime - visitLoginUrlTime > YueCheHelper.SESSION_TIMEOUT_MILLISECOND) ){
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
			
			do{
				 result = httpUtil4.post(LOGIN_URL, json);
				 if (result == null){
					 ThreadUtil.sleep(YueCheHelper.MAX_SLEEP_TIME);
				 }else{
					 break;
				 }
			
			}while(result == null);
			
		
			if (result != null) {
				System.out.println(result);
				log.debug(result);
				//登录成功
				if (result.equals("/index.aspx")) {
					isLoginSuccess =true;
				} else if(result.indexOf("验证码错误")!= -1 ||result.indexOf("请输入验证码")!= -1 ){  //失败的话 ，继续登录
					System.out.println("验证码识别错误！登录失败.");
				}else if(result.indexOf("账号或密码错误")!= -1  ){  //失败的话 ，继续登录
					System.out.println("账号或密码错误！登录失败.");
					log.error("账号或密码错误"); //打印错误，直接退出
					System.exit(1);
				}else if (result.indexOf("系统服务时间每天从07:35-20:00")!= -1  ){
					System.out.println("系统服务时间每天从07:35-20:00;"+"enter sleep");
					ThreadUtil.sleep (YueCheHelper.MIN_SCAN_INTEVAL +RandomUtil.getRandomInt(YueCheHelper.MAX_SCAN_INTEVAL-YueCheHelper.MIN_SCAN_INTEVAL));
				}else{
					
				}
//				
			 
			}
		}while(!isLoginSuccess);
	
		return true;
	}

	/**
	 * -1 约车错误 0 约车成功 1 无车可约
	 * 
	 */
	public  int yuche(String date, String amOrpm, boolean isGetHiddenKM)
			throws InterruptedException {

		int result = UNKNOWN_ERROR;

		// 页面中一个隐藏的输入，默认为2，可能更改
		String hiddenKM = "2";
		if (isGetHiddenKM) {
			String yuchePage = httpUtil4.getContent(YUCHE_URL);
			Document document = Jsoup.parse(yuchePage);
			Element hkm = document.getElementById(HIDDEN_KM);
			hiddenKM = hkm.attr("value");
		}

		// {"yyrq":"20121126","yysd":"58","xllxID":"2","pageSize":35,"pageNum":1}
		JSONObject json = new JSONObject();
		try {
			json.put("yyrq", date);
			json.put("yysd", amOrpm);
			json.put("xllxID", hiddenKM);
			json.put("pageSize", 35);
			json.put("pageNum", 1);
			
			JSONObject carsJson = null;
			do{
				// 得到某天的信息
				carsJson = httpUtil4.postJson(GET_CARS_URL, json);
				if (carsJson == null) {
					log.error("get car info error");
				}else{
					break;
				}
			}while(true);
			

			// System.out.println(carsJson.toString());

			JSONObject selectedCar = null;

			String data = carsJson.getString("d");

			int splitPosition = data.indexOf("_");
			String carInfo = data.substring(0, splitPosition);
			String nu = data.substring(splitPosition + 1);
			int totalNum = Integer.valueOf(nu);

			// {
			//
			// "YYRQ": "20121126",
			//
			// "XNSD": "58",
			//
			// "CNBH": "06143"
			//
			// },

			JSONArray carsArray = new JSONArray(carInfo);
			System.out.println("可选的车有：" + carsArray.toString());
			if (carsArray.length() == 0) {
				result = NO_CAR;
				return NO_CAR;
			}

			// GetCar over

			// 下一步，约车
			int yucheTry = 0;

			do {
				selectedCar = carsArray.getJSONObject(RandomUtil.getRandomInt(carsArray.length()));

				if (selectedCar != null) {
					System.out.println("选择的车是：" + selectedCar.toString());
					String imageCode = "";
					try {
						do{
							imageCode = getImgCode(BOOKING_IMG_URL);
						}while(imageCode == null);
						
					} catch (IOException e1) {
						log.error("get book image code error", e1);
						continue;
					}

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

						e.printStackTrace();
					}
					
					ThreadUtil.sleep(1);
					//一直重试，知道返回结果
					JSONObject bookResult = null;
					do{
						bookResult = httpUtil4.postJson(BOOKING_CAR_URL, bookCarJson);
						if (bookResult == null) {
							System.out.println("book car timeout or error");
							log.error("book car timeout or error");
							ThreadUtil.sleep(1);
						}
					}while(bookResult == null);
					
					yucheTry++;
					

					System.out.println(bookResult.toString());

					JSONArray jbResult = new JSONArray(bookResult.getString("d"));

					// {"d":"[\r\n  {\r\n    \"Result\": true,\r\n    \"OutMSG\": \"\"\r\n  }\r\n]"}

					if (jbResult.getJSONObject(0).getBoolean("Result")) {
						System.out.println("预约成功!...");
						String info = "车辆信息:"+selectedCar.getString("YYRQ") + ":"
								+ selectedCar.getString("XNSD") + "-"
								+ selectedCar.getString("CNBH");
						System.out.println(info);
						log.info(info);
						result = BOOK_CAR_SUCCESS;
					} else {
						//TODO 如果今天已经约车车辆
						String outMsg = jbResult.getJSONObject(0).getString("OutMSG");
						if ("该日已预约过小时".equals(outMsg)){
							result = ALREADY_BOOKED_CAR;
							break;
						}
						if("验证码错误！".equals(outMsg)){
							System.out.println(outMsg+"不计入retry次数");
							yucheTry--; //验证码错误，不计入retry次数
						}
						System.out.println("book car return error:"+outMsg);
						log.error("book car return error:"+outMsg);
					}

				}
			} while (result != BOOK_CAR_SUCCESS && yucheTry <= YUCHE_RETRY_TIME);
		} catch (JSONException e) {
			log.error("error,", e);
			e.printStackTrace();
		}

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
	private  String getImgCodeManual(String url) throws IOException {

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

		System.out.println("请输入验证码\r\n");

		do {
			BufferedReader strin2 = new BufferedReader(new InputStreamReader(
					System.in));
			imageCode = strin2.readLine().trim();
			System.out.println("输入为:" + imageCode + "; 正确按任意键，错误按R/r \r\n");
			String command = strin2.readLine().trim().toLowerCase();
			if (!command.equals("r")) {
				break;
			}
			System.out.println("重新输入\r\n");
		} while (true);

		IO.deleteFile(storeAddress);

		return imageCode;
	}
	
	
	private String getImgCode(String url)throws IOException{
		if (YueCheHelper.IMAGE_CODE_INPUT_METHOD_IS_AUTO){
			return getImgCodeAuto(url);
		}else{
			return	getImgCodeManual(url);
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

		comand =  "tesseract "+ destAddress +" " + textImg ; //命令行

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
	
	private   String getYueCheInfo(){
		String yuchePage = null;
		do{
			 yuchePage = httpUtil4.getContent(YUCHE_URL);
			 
		}while(yuchePage == null);
		if (yuchePage.equals(" /login.aspx")){
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
			System.out.println();
		}
		return "";
	}
	/**
	 * 0 可以
	 * 1 该日已经约车
	 * 2 无车
	 * 3 登录超时
	 */
	public int canYueChe (String yueCheDate,  String amPm){
		String yueCheInfo = getYueCheInfo();
		if (yueCheInfo.equals("noLogin")){
			return 3;
		}
	   return 0;
	}
}
