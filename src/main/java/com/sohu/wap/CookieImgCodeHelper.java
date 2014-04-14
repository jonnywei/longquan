/**
 *@version:2012-11-30-下午02:39:30
 *@author:jianjunwei
 *@date:下午02:39:30
 *
 */
package com.sohu.wap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.VerifyCode;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.util.RandomUtil;

/**
 * 准备image code和 book code
 * @author jianjunwei
 *
 */
public class CookieImgCodeHelper {

    
    
    private static Logger log = LoggerFactory.getLogger(CookieImgCodeHelper.class);
    
    
    private static  Map<String,  VerifyCode >   IMAGE_CODE_COOKIE =  new HashMap<String,  VerifyCode > () ;
    
    private static Map<String, List<VerifyCode>> map;
    
    public static String COOKIE_IMG_CODE_KEY="ImgCode";
    

    public static String COOKIE_LOGINON_KEY ="LoginOn";
    
    public static String COOKIE_ASP_NET_SESSION_ID_KEY ="ASP.NET_SessionId";
    
    private static boolean isGetCookieImgFromNet = false;
    
    public static synchronized VerifyCode getVerifyCodeSmart(String type){
    	if (   isGetCookieImgFromNet){
        	 map = NetCookieImgCodeHelper.getCookieImgCode();
        	 isGetCookieImgFromNet =true;

    	}
    	if(map== null || map.get(VerifyCode.CODE_TYPE_BOOKING_CODE).isEmpty() || map.get(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE).isEmpty()){
    		log.error("get net cookie img code is error,enter manul model");
    		
    		if (type.equals(VerifyCode.CODE_TYPE_BOOKING_CODE)){
    			VerifyCode vc = new  VerifyCode("fws3", "9PG/u8TJ/4g=");
        		vc.setAspSessionId("x1ub05dwasx1kbxtjnopmoiu");
        		 		
                vc.setCodeType(VerifyCode.CODE_TYPE_BOOKING_CODE);
                return vc;
    		}
//    		pphc 	82wpgRb52lg=	c355lwdkyyo54h4y1mbtjbx3 	
//    		VerifyCode vc = new  VerifyCode("tvdb", "uWAlSC166Gs=");
//    		VerifyCode vc = new  VerifyCode("mcbn", "4819A8chX0o=");
//    		vc.setAspSessionId("kkwswuuhsg420ivma3vktogu");
//    		cg7n 	SjdheqvPtCw=	c355lwdkyyo54h4y1mbtjbx3 	 8nfs 	C53EXOfOhA8=
    		VerifyCode vc = new  VerifyCode("fws3", "9PG/u8TJ/4g=");
    		vc.setAspSessionId("x1ub05dwasx1kbxtjnopmoiu");
    	
            vc.setCodeType(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE);
            
            return vc;
//    		return	getImageCodeCookie2(type);
    	}else{
    		List<VerifyCode> vcList = map.get(type);
    		return vcList.get(RandomUtil.getRandomInt(vcList.size()));
    	}
    	
    	
    }
    
    
    
    private static boolean isGeted = false;
    
    private static long  lastVisitedTime =0;
    
   public static void    getImageCodeCookie( ){
        
	   getImageCodeCookie2("");
        
    }
    
   public static synchronized  VerifyCode  getImageCodeCookie(String type){
	  return getVerifyCodeSmart(type);
   }
    private static synchronized  VerifyCode  getImageCodeCookie2(String type){
        
        long currentTime = System.currentTimeMillis();
        
        //为了加快访问速度，只加载一次login页面
        //如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
        if (  !isGeted || (currentTime - lastVisitedTime > YueCheHelper.IMAGE_CODE_TIMEOUT_MILLISECOND) ){
            prepareImageCode();
            isGeted = true;
            lastVisitedTime = System.currentTimeMillis();
        }
      
        return IMAGE_CODE_COOKIE.get(type);
        
    }
    
    
    
    /**
     *准备image code和 book code
     * 
     */
    private  static void prepareImageCode(){
        
        HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient();
        
        YueChe nobody = new YueChe();
       
        nobody.setHttpUtil4(httpUtil4);
        
        String imageCode = null;
        
        do{
            try {
                imageCode =   nobody.getImgCodeManual(YueChe.LOGIN_IMG_URL);
            } catch (IOException e) {
                log.error("getCode error",e);
            }
          
        }while(imageCode == null || imageCode.length() < 4 );
        
        String cookieValue =  httpUtil4.getCookieValue(COOKIE_IMG_CODE_KEY);
        VerifyCode vc = new  VerifyCode(imageCode, cookieValue);
        vc.setCodeType(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE);
        System.out.println(httpUtil4.getCookieValue(COOKIE_ASP_NET_SESSION_ID_KEY));
        CookieImgCodeHelper.IMAGE_CODE_COOKIE.put(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE, vc);
        
        imageCode = null;
        
        do{
            try {
                imageCode =   nobody.getImgCodeManual(YueChe.BOOKING2_IMG_URL);
            } catch (IOException e) {
                log.error("get Booking Code error",e);
            }
          
        }while(imageCode == null || imageCode.length() != 1 );
        
        
        String bcookieValue =  httpUtil4.getCookieValue(COOKIE_IMG_CODE_KEY);
        
        VerifyCode bcookie = new  VerifyCode(imageCode, bcookieValue);
        bcookie.setCodeType(VerifyCode.CODE_TYPE_BOOKING_CODE);
        CookieImgCodeHelper.IMAGE_CODE_COOKIE.put(VerifyCode.CODE_TYPE_BOOKING_CODE, bcookie);
        
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        getImageCodeCookie("");
        Iterator it =  CookieImgCodeHelper.IMAGE_CODE_COOKIE.keySet().iterator();
        while (it.hasNext()){
        	System.out.println(CookieImgCodeHelper.IMAGE_CODE_COOKIE.get(it.next()));
        }
       
    }

}
