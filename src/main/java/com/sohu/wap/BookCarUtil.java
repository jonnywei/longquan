package com.sohu.wap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.bo.Result;

public class BookCarUtil {
	
	private static Logger log = LoggerFactory.getLogger(BookCarUtil.class);
	public static int UNDEFINE = -1000;
	public static int RETURN_FORMAT_ERROR = -2;
	public static int UNKNOWN_ERROR = -1;
	
	public static int NO_CAR = 1;
	public static int GET_CAR_ERROR = 2;
	public static int TODAY_ALREADY_BOOKED_CAR = 3;
	public static int BOOK_CAR_TIMEOUT = 4;
	public static int BOOK_INVAILD_OPERATION = 5;
	public static int BOOK_CAR_IMAGE_CODE_ERROR = 6;
	public static int CAR_BOOKED = 7;
	public static int SELECT_CAR_ERROR = 8;
	public static int HAVE_CAR = 100;
	public static int IP_FORBIDDEN = 101;
	public static int SERVER_ERROR =102;

	public static Result<JSONArray> carsResult(JSONObject carsJson) {
		Result<JSONArray> ret = new Result<JSONArray>(HAVE_CAR);

		if (carsJson == null) {
			ret.setRet(GET_CAR_ERROR);
			return ret;
		}

		// System.out.println(carsJson.toString());

		try {
			String data = carsJson.getString("d");
			
			System.out.println("carInfo:" + data);

			if (data.equals("LoginOut:您的IP地址被禁止!")) {
				log.error("LoginOut:您的IP地址被禁止!");
				ret.setRet(IP_FORBIDDEN);
				return ret;
			}

			int splitPosition = data.indexOf("_");
			String carInfo = data.substring(0, splitPosition);
			String nu = data.substring(splitPosition + 1);

			int totalNum = Integer.valueOf(nu);

			log.info("totalPage:" + totalNum);
			// {
			//
			// "YYRQ": "20121126",
			//
			// "XNSD": "58",
			//
			// "CNBH": "06143"
			//
			// },

			JSONArray carsArray = new JSONArray(carInfo);
			System.out.println("可选的车有：" + carsArray.toString());
			log.info("availableCar：" + carsArray.toString());
			if (carsArray.length() == 0) {
				ret.setRet(BookCarUtil.NO_CAR);
			} else {
				ret.setData(carsArray);
				ret.setExtend(totalNum);
				ret.setRet(BookCarUtil.HAVE_CAR);
			}

		} catch (JSONException e) {
			ret.setRet(RETURN_FORMAT_ERROR);
			log.error("return json error", e);
		}
		return ret;
	}

	public static int bookResult(JSONObject bookResult) {

		int resultN = UNKNOWN_ERROR;
		try {

			if (bookResult == null) {
				System.out.println("book car timeout or error");
				log.error("book car timeout or error");
				resultN = BOOK_CAR_TIMEOUT;
				return resultN;
			}
			// {"d":"[\r\n  {\r\n    \"Result\": true,\r\n    \"OutMSG\": \"\"\r\n  }\r\n]"}

			System.out.println(bookResult.toString());

			JSONArray jbResult;

			jbResult = new JSONArray(bookResult.getString("d"));

			if (jbResult.getJSONObject(0).getBoolean("Result")) {
				resultN = YueCheItem.BOOK_CAR_SUCCESS;

			} else {
				String outMsg = jbResult.getJSONObject(0).getString("OutMSG");
				log.info("book car return msg:" + outMsg);
				if ("该日已预约过小时".equals(outMsg)) {
					resultN = TODAY_ALREADY_BOOKED_CAR;
				} else if ("非法操作".equals(outMsg)) {
					resultN = BOOK_INVAILD_OPERATION;
				} else if ("验证码错误！".equals(outMsg)) {
					resultN = BOOK_CAR_IMAGE_CODE_ERROR;
				} else if (outMsg.indexOf("该车时段已经被约") != -1) {
					resultN = CAR_BOOKED;
				} else {
					resultN = UNKNOWN_ERROR;
					log.error("book car return error:" + outMsg);
				}

			}
		} catch (JSONException e) {
			resultN = RETURN_FORMAT_ERROR;
			log.error("return json error", e);
		}
		return resultN;
	}
}
