package com.sohu.wap.util;
import org.json.JSONException;

import com.sohu.wap.http.HttpUtil4Exposer;

/**
 *@version:2012-12-5-下午01:33:14
 *@author:jianjunwei
 *@date:下午01:33:14
 *
 */

/**
 * 根据ip138的结果，得到当前的ip地址
 * 
 * @author jianjunwei
 *
 */
public class IpUtil {
    
    private static  String myIp = null;
    static  String url ="http://iframe.ip138.com/ic.asp";
    public static  String getMyIp(){
        if (myIp  == null){
            synchronized (IpUtil.class){
                if (myIp == null){
                    HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient();
                    String result = httpUtil4.getContentAutoSelectCharSet(url);
                    if (result != null){
                       int start = result.indexOf("[");
                       int end  = result.indexOf("]");
                       myIp = result.substring(start+1, end);
                       
                    }
                }
            }
        }
      
       return myIp;
    }
    public static void main(String[] args) throws JSONException {
           System.out.println(getMyIp());
    }
}
