import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.sohu.wap.YueCheHelper;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imageCode=")( dddE6'GZ";
		imageCode=imageCode.replace(")(", "X");
		imageCode =imageCode.replaceAll("[^0-9a-zA-Z]", "");
		System.out.println(imageCode);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String name = "LoginOn";
		String value ="";
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setPath("/");
		cookie.setVersion(1);
		
		httpClient.getCookieStore().addCookie(cookie);
		  System.out.println( "2012-12-10".replace("-", ""));
	
		String[] timeArray = "am,pm,ni".split("[,;]");
        if (timeArray.length  <  0) {
        	timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
        }
        
        for (String amPm : timeArray){  //按情况约车
        	amPm =  YueCheHelper.AMPM.get(amPm);
        	System.out.println(amPm);
        }
	}

}
