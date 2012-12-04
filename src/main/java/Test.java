import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import com.sohu.wap.YueCheHelper;
import com.sohu.wap.http.HttpUtil4;


public class Test {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		String imageCode=")( dddE6'GZ";
		imageCode=imageCode.replace(")(", "X");
		imageCode =imageCode.replaceAll("[^0-9a-zA-Z]", "");
		System.out.println(imageCode);
		
//		DefaultHttpClient httpClient = new DefaultHttpClient();
//		String name = "LoginOn";
//		String value ="";
//		BasicClientCookie cookie = new BasicClientCookie(name, value);
//		cookie.setPath("/");
//		cookie.setVersion(1);
//		
//		httpClient.getCookieStore().addCookie(cookie);
//		  System.out.println( "2012-12-10".replace("-", ""));
//	
//		String[] timeArray = "am,pm,ni".split("[,;]");
//        if (timeArray.length  <  0) {
//        	timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
//        }
//        
//        for (String amPm : timeArray){  //按情况约车
//        	amPm =  YueCheHelper.AMPM.get(amPm);
//        	System.out.println(amPm);
//        }
//        
        
        YueCheHelper.IS_USE_PROXY = false;
        String url ="http://localhost:8888/bookcar";
        JSONObject json = new JSONObject();
       
            json.put("yyrq", "date");
            json.put("yysd",  "amOrpm");
            json.put("xllxID",  "hiddenKM");
            json.put("pageSize", 35);
            json.put("pageNum", 1);
        
            JSONObject result = HttpUtil4.getInstanceHaveCookie().postJson(url, json);
       
	}

}
