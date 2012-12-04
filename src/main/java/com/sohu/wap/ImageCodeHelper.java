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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.VerifyCode;
import com.sohu.wap.http.HttpUtil4Exposer;

/**
 * 准备image code和 book code
 * @author jianjunwei
 *
 */
public class ImageCodeHelper {

    
    
    private static Logger log = LoggerFactory.getLogger(ImageCodeHelper.class);
    
    
    private static Map <String ,VerifyCode> IMAGE_CODE_COOKIE = new HashMap<String,VerifyCode>();
    
    public static String COOKIE_IMG_CODE_KEY ="ImgCode";
    
    public static String COOKIE_BOOKING_CODE_KEY ="BookingCode";
    
    public static String COOKIE__LOGINON_KEY ="LoginOn";
    
    public static String COOKIE_ASP_NET_SESSION_ID_KEY ="ASP.NET_SessionId";
    
    
    
    private static boolean isGeted = false;
    
    private static long  lastVisitedTime =0;
    
    public static synchronized  Map <String ,VerifyCode> getImageCodeCookie(){
        
        long currentTime = System.currentTimeMillis();
        
        //为了加快访问速度，只加载一次login页面
        //如果没有访问过登录页面，或者上次访问登录页面超过了超时时间
        if (  !isGeted || (currentTime - lastVisitedTime > YueCheHelper.IMAGE_CODE_TIMEOUT_MILLISECOND) ){
            prepareImageCode();
            isGeted = true;
            lastVisitedTime = System.currentTimeMillis();
        }
      
        return IMAGE_CODE_COOKIE;
        
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
        
        String cookieValue =  httpUtil4.getCookieValue(ImageCodeHelper.COOKIE_IMG_CODE_KEY);
       
       
        VerifyCode vc = new  VerifyCode(imageCode, cookieValue);
        
        ImageCodeHelper.IMAGE_CODE_COOKIE.put(ImageCodeHelper.COOKIE_IMG_CODE_KEY, vc);
        
        imageCode = null;
        
        do{
            try {
                imageCode =   nobody.getImgCodeManual(YueChe.BOOKING_IMG_URL);
            } catch (IOException e) {
                log.error("get Booking Code error",e);
            }
          
        }while(imageCode == null || imageCode.length() < 4 );
        
        
        String bcookieValue =  httpUtil4.getCookieValue(ImageCodeHelper.COOKIE_BOOKING_CODE_KEY);
        
        VerifyCode bcookie = new  VerifyCode(imageCode, bcookieValue);
        
        ImageCodeHelper.IMAGE_CODE_COOKIE.put(ImageCodeHelper.COOKIE_BOOKING_CODE_KEY, bcookie);
        
        
        
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        getImageCodeCookie();
        Iterator it =  ImageCodeHelper.IMAGE_CODE_COOKIE.keySet().iterator();
       
    }

}
