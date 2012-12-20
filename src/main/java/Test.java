import org.json.JSONException;

import com.sohu.wap.proxy.Host;
import com.sohu.wap.proxy.ProxyHelper;

public class Test {

    /**
     * @param args
     * @throws JSONException 
     */
    public static void main(String[] args) throws JSONException {
        // TODO Auto-generated method stub
//        String imageCode = ")( dddE6'GZ";
//        imageCode = imageCode.replace(")(", "X");
//        imageCode = imageCode.replaceAll("[^0-9a-zA-Z]", "");
//        System.out.println(imageCode);

        // DefaultHttpClient httpClient = new DefaultHttpClient();
        // String name = "LoginOn";
        // String value ="";
        // BasicClientCookie cookie = new BasicClientCookie(name, value);
        // cookie.setPath("/");
        // cookie.setVersion(1);
        //		
        // httpClient.getCookieStore().addCookie(cookie);
        // System.out.println( "2012-12-10".replace("-", ""));
        //	
        // String[] timeArray = "am,pm,ni".split("[,;]");
        // if (timeArray.length < 0) {
        // timeArray = YueCheHelper.YUCHE_TIME.split("[,;]");
        // }
        //        
        // for (String amPm : timeArray){ //按情况约车
        // amPm = YueCheHelper.AMPM.get(amPm);
        // System.out.println(amPm);
        // }
        //        
      
    	System.out.print("\\a");
        String proxyIp = "115.236.98.101";
        int port = 80;
        String[] ipArray = new String[] { "61.55.141.10", "125.39.66.150", "221.176.14.72", "125.39.66.147",
                "115.236.98.101", "122.72.2.180","221.176.14.72","125.39.66.147"

        };
       
        for (int i = 0; i < ipArray.length; i++) {
            proxyIp = ipArray[i];
             
            if (ProxyHelper.testProxy(proxyIp, port)) {
                System.out.println(proxyIp + " check ok!");

            } else {
                System.err.println(proxyIp + "check  error!");

            }
        }
        final 	Host host = new Host(proxyIp, port);
        host.setAnonymity("AMXXX");
        System.out.println(host);
//        JSONObject json = new JSONObject();
//
//        json.put("yyrq", "date");
//        json.put("yysd", "amOrpm");
//        json.put("xllxID", "hiddenKM");
//        json.put("pageSize", 35);
//        json.put("pageNum", 1);
//
//        Iterator iterator = HttpProxy.getHttpProxy().keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = (String) iterator.next();
//            Host host = HttpProxy.getHttpProxy().get(key);
//
//            HttpUtil4Exposer httpUtil4 = HttpUtil4Exposer.createHttpClient(host.getIp(), host.getPort());
//            httpUtil4.addCookie("client_cookie", "client_cookie", ".sohu.com");
//            String result = httpUtil4.getContent(url);
//
//            if (httpUtil4.getCookieValue("client_cookie").equals("client_cookie")
//                    && httpUtil4.getCookieValue("cookie_test") != null
//                    && httpUtil4.getCookieValue("cookie_test").equals("true")) {
//                System.out.println(host + "check ok!");
//
//            } else {
//                System.out.println(host + "cookie  test error,remove");
//
//            }
//        }

    }

}
