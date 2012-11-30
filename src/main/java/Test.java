import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;


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
	}

}
