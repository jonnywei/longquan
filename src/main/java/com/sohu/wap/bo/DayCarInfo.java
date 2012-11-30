/**
 *@version:2012-11-29-下午01:57:41
 *@author:jianjunwei
 *@date:下午01:57:41
 *
 */
package com.sohu.wap.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * 每日车辆信息
 * @author jianjunwei
 *
 */
public class DayCarInfo {
    
    private String date;
    private Map<String, String> carInfo = new  HashMap<String, String>();
    
    private String amCarInfo;
    private String pmCarInfo;
    private String niCarInfo;
    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    /**
     * @return the amCarInfo
     */
    public String getAmCarInfo() {
        return amCarInfo;
    }
    /**
     * @param amCarInfo the amCarInfo to set
     */
    public void setAmCarInfo(String amCarInfo) {
        this.amCarInfo = amCarInfo;
    }
    /**
     * @return the pmCarInfo
     */
    public String getPmCarInfo() {
        return pmCarInfo;
    }
    /**
     * @param pmCarInfo the pmCarInfo to set
     */
    public void setPmCarInfo(String pmCarInfo) {
        this.pmCarInfo = pmCarInfo;
    }
    /**
     * @return the niCarInfo
     */
    public String getNiCarInfo() {
        return niCarInfo;
    }
    /**
     * @param niCarInfo the niCarInfo to set
     */
    public void setNiCarInfo(String niCarInfo) {
        this.niCarInfo = niCarInfo;
    }
	public void setCarInfo(Map<String, String> carInfo) {
		this.carInfo = carInfo;
	}
	public Map<String, String> getCarInfo() {
		return carInfo;
	}
    
    
}
