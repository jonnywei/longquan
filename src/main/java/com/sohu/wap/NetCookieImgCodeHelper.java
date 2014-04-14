package com.sohu.wap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.VerifyCode;
import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;

/**
 * 从网络获得预先设定的cookie
 * 
 * @author wjj
 * 
 */
public class NetCookieImgCodeHelper {

	private static Logger log = LoggerFactory
			.getLogger(NetCookieImgCodeHelper.class);

	private static Map<String, List<VerifyCode>> IMAGE_CODE_COOKIE = new HashMap<String, List<VerifyCode>>();

	static {
		List<VerifyCode> imageCodeList = new LinkedList<VerifyCode>();
		List<VerifyCode> bookCodeList = new LinkedList<VerifyCode>();
		IMAGE_CODE_COOKIE.put(VerifyCode.CODE_TYPE_LOGIN_IMG_CODE,
				imageCodeList);
		IMAGE_CODE_COOKIE.put(VerifyCode.CODE_TYPE_BOOKING_CODE, bookCodeList);

	}

	public static Map<String, List<VerifyCode>> getCookieImgCode() {

		JSONArray ycArray = new JSONArray();
		try {

			String cookieInfo = HttpUtil4Exposer.getInstance().getContent(
					Constants.COOKIE_URL);
			if (cookieInfo != null) {
				ycArray = new JSONArray(cookieInfo);

			} else {
				log.error("get yuche info from server error");

			}
			for (int index = 0; index < ycArray.length(); index++) {

				JSONObject yc = ycArray.getJSONObject(index);

				VerifyCode vc = VerifyCode.jsonToVerifyCode(yc);
				//   		
				List<VerifyCode> vcList = IMAGE_CODE_COOKIE.get(vc
						.getCodeType());

				vcList.add(vc);

			}
		} catch (Exception ex) {
			log.error("catch exception", ex);
		}

		return IMAGE_CODE_COOKIE;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetCookieImgCodeHelper.getCookieImgCode();
	}

}
