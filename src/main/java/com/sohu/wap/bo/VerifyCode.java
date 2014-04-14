package com.sohu.wap.bo;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyCode {
	private String vcode;
	private String cookie;
	private int valid;
	private String codeType;
	private Date createDate;
	private String aspSessionId;
	
	
	public static String CODE_TYPE_LOGIN_IMG_CODE="ImgCode";
	public static String CODE_TYPE_BOOKING_CODE="BookingCode";

	public VerifyCode(String code, String cookie) {
		this.vcode = code;
		this.cookie = cookie;
	}
//	{
//	    "pk": 1,
//	    "model": "settings.cookieimgcode",
//	    "fields": {
//	        "vcode": "xsc7",
//	        "update_date": "2014-04-12T13:49:31Z",
//	        "code_type": "ImgCode",
//	        "create_date": "2014-04-12T13:49:31Z",
//	        "valid": 1,
//	        "cookie": "5GkTqIwLV8M="
//	    }
//	}
	public static VerifyCode jsonToVerifyCode(JSONObject json){
		if(json == null){
			return null;
		}
		try {
			JSONObject fields = json.getJSONObject("fields");
			
			VerifyCode v = new VerifyCode(fields.getString("vcode"), fields.getString("cookie"));
			v.setValid(fields.getInt("valid"));
			v.setCodeType(fields.getString("code_type"));
			v.setAspSessionId(fields.getString("asp_session_id"));
//			v.setCreateDate(fields.getString(arg0))
			return v;
		}catch (JSONException ex){
			return null;
		}
		

	}

	/**
	 * @param cookie
	 *            the cookie to set
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * @param vcode
	 *            the vcode to set
	 */
	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	/**
	 * @return the vcode
	 */
	public String getVcode() {
		return vcode;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getAspSessionId() {
		return aspSessionId;
	}
	public void setAspSessionId(String aspSessionId) {
		this.aspSessionId = aspSessionId;
	}

}