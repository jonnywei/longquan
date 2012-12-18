/**
 *@version:2012-12-4-下午06:17:44
 *@author:jianjunwei
 *@date:下午06:17:44
 *
 */
package com.sohu.wap;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.sohu.wap.http.HttpUtil4;

/**
 * @author jianjunwei
 *
 */
public class GAEBookCar {

//    private  static String GAE_BOOK_CAR_URL ="http://haijiayc.appspot.com/bookcar";
    private  static String GAE_BOOK_CAR_URL ="http://dev.w.sohu.com/t2/home.do";
    
    public static JSONObject book(JSONObject carInfo , JSONObject cookieInfo ){
        
        JSONObject all = new JSONObject();
      
        try {
            all.put("car", carInfo);
            all.put("cookie",cookieInfo);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  HttpUtil4.getInstance("YueCheHelper.GAE_PROXY_IP",80).postJson(GAE_BOOK_CAR_URL, all);
      
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
       
       System.out.println(GAEBookCar.book(new JSONObject(), new JSONObject()).toString()); 
    }
}
