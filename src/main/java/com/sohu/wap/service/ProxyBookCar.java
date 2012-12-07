/**
 *@version:2012-12-4-下午06:17:44
 *@author:jianjunwei
 *@date:下午06:17:44
 *
 */
package com.sohu.wap.service;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sohu.wap.CookieImgCodeHelper;
import com.sohu.wap.YueChe;
import com.sohu.wap.http.HttpUtil4;
import com.sohu.wap.http.HttpUtil4Exposer;
import com.sohu.wap.proxy.Host;
import com.sohu.wap.proxy.SpysHttpProxy;
import com.sohu.wap.util.RandomUtil;

/**
 * @author jianjunwei
 *
 */
public class ProxyBookCar extends YueChe {

    

    public static JSONObject book(JSONObject carInfo , JSONObject cookieInfo ){
       
       Set<Entry<String, Host>> set =  SpysHttpProxy.getInstance().getProxy().entrySet();
       Set<Entry<String, Host>> [] array =   (Set<Entry<String, Host>>[]) set.toArray();
     
       Entry<String, Host>  entry =    (Entry<String, Host>) array[RandomUtil.getRandomInt(array.length)];
      
       
       Host host = entry.getValue();
       
       HttpUtil4Exposer httpUtil =   HttpUtil4Exposer.getProxyInstance(host.getIp(), host.getPort());
      
       httpUtil.addCookie(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY, cookieInfo.optString(CookieImgCodeHelper.COOKIE_IMG_CODE_KEY));
       httpUtil.addCookie(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY, cookieInfo.optString(CookieImgCodeHelper.COOKIE_BOOKING_CODE_KEY));
       httpUtil.addCookie(CookieImgCodeHelper.COOKIE_LOGINON_KEY, cookieInfo.optString(CookieImgCodeHelper.COOKIE_LOGINON_KEY));
       httpUtil.addCookie(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY, cookieInfo.optString(CookieImgCodeHelper.COOKIE_ASP_NET_SESSION_ID_KEY));
      
       return  httpUtil.postJson(BOOKING_CAR_URL, carInfo);
      
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
       
       System.out.println(ProxyBookCar.book(new JSONObject(), new JSONObject()).toString()); 
    }
}
