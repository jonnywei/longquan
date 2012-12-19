/**
 *@version:2012-11-26-下午06:35:49
 *@author:jianjunwei
 *@date:下午06:35:49
 *
 */
package com.sohu.wap;

import org.json.JSONObject;

import com.sohu.wap.core.Constants;

/**
 * 
 * @author jianjunwei
 *
 */
public class XueYuanAccount {
	
	
	
	public static int BOOK_CAR_SUCCESS = 0;
	
	public static int BOOK_CAR_ALREADY_BOOKED_CAR=1;
	
    //1开始
    public static int BOOK_CAR_NOT_SET = 2;

    
	public static int BOOK_CAR_KEMU2_NO_TIME = 3;
	
    
    public static int BOOK_CAR_NOT_BOOK_WEEKEND_CAR= 4;
    
    public static int BOOK_CAR_ERROR = 5;
    
   
	
	private int id;
    
    private String userName;
    
    private String password;
    
    private String carType;
    
    private String yueCheDate;
    
    private String amPm;
    
    private String whiteCar;
    
    private String blackCar;
    
    private String km ="km2";
    
    private int ycResult;
    
    private String ycResultInfo;
    
    
    private boolean isBookSuccess = false;
    
    
//  [
//  {
//      "pk": 1,
//      "model": "yueche.yueche",
//      "fields": {
//          "car_type": "als",
//          "yc_date": "2012-12-25",
//          "black_car": "",
//          "passwd": "23456789",
//          "id_num": "234567890-",
//          "xue_yuan": 1,
//          "yc_info": "",
//          "white_car": "",
//          "yc_km": "km2",
//          "yc_result": null,
//          "phone_num": "",
//          "create_date": "2012-12-18T15:09:51Z",
//          "yc_time": "am",
//          "update_date": "2012-12-18T15:17:29Z",
//          "reserve": ""
//      }
//  }
//]
 
    public static  XueYuanAccount jsonToXueYuanAccount(JSONObject json){
    	
    	XueYuanAccount yc= new XueYuanAccount();
    	
    	yc.setId(json.optInt("pk"));
    	JSONObject field = json.optJSONObject("fields");
    	yc.setUserName(field.optString("id_num").toUpperCase());
    	yc.setPassword(field.optString("passwd"));
    	yc.setKm(field.optString("yc_km",Constants.KM2));
    	yc.setAmPm(field.optString("yc_time"));
    	yc.setCarType(field.optString("car_type"));
    	yc.setWhiteCar(field.optString("white_car"));
    	yc.setBlackCar(field.optString("black_car"));
    	if(field.isNull("yc_result")){
    		yc.setYcResult(BOOK_CAR_NOT_SET);
    	}else{
    		yc.setYcResult(field.optInt("yc_result"));
    	}
    	yc.setYcResultInfo(field.optString("yc_info"));
    	
    	return yc;
    }
    
    
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the isBookSuccess
     */
    public boolean isBookSuccess() {
        return isBookSuccess;
    }
    /**
     * @param isBookSuccess the isBookSuccess to set
     */
    public void setBookSuccess(boolean isBookSuccess) {
        this.isBookSuccess = isBookSuccess;
    }
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getYueCheDate() {
		return yueCheDate;
	}
	public void setYueCheDate(String yueCheDate) {
		this.yueCheDate = yueCheDate;
	}
	public String getYueCheAmPm() {
		return amPm;
	}
	public void setYueCheAmPm(String amPm) {
		this.amPm = amPm;
	}
    /**
     * @param whiteCar the whiteCar to set
     */
    public void setWhiteCar(String whiteCar) {
        this.whiteCar = whiteCar;
    }
    /**
     * @return the whiteCar
     */
    public String getWhiteCar() {
        return whiteCar;
    }
	public String getAmPm() {
		return amPm;
	}
	public void setAmPm(String amPm) {
		this.amPm = amPm;
	}
	public String getKm() {
		return km;
	}
	public void setKm(String km) {
		this.km = km;
	}
	@Override
	public String toString() {
		return "XueYuanAccount [amPm=" + amPm + ", blackCar=" + blackCar
				+ ", carType=" + carType + ", id=" + id + ", isBookSuccess="
				+ isBookSuccess + ", km=" + km + ", password=" + password
				+ ", userName=" + userName + ", whiteCar=" + whiteCar
				+ ", yueCheDate=" + yueCheDate + "]";
	}
	public String getBlackCar() {
		return blackCar;
	}
	public void setBlackCar(String blackCar) {
		this.blackCar = blackCar;
	}
	public int getYcResult() {
		return ycResult;
	}
	public void setYcResult(int ycResult) {
		this.ycResult = ycResult;
	}
	public String getYcResultInfo() {
		return ycResultInfo;
	}
	public void setYcResultInfo(String ycResultInfo) {
		this.ycResultInfo = ycResultInfo;
	}
}
