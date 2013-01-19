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
 * 每日考试信息
 * @author jianjunwei
 *
 */
public class DayKaoShiInfo {
    
    private String date;
   
    private String amPm;
    private String remindNum;
    private String kaoShi;
    
    private String status;
 
    
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
     * @return the amPm
     */
    public String getAmPm() {
        return amPm;
    }
    /**
     * @param amPm the amPm to set
     */
    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }
    /**
     * @return the remindNum
     */
    public String getRemindNum() {
        return remindNum;
    }
    /**
     * @param remindNum the remindNum to set
     */
    public void setRemindNum(String remindNum) {
        this.remindNum = remindNum;
    }
    /**
     * @return the kaoShi
     */
    public String getKaoShi() {
        return kaoShi;
    }
    /**
     * @param kaoShi the kaoShi to set
     */
    public void setKaoShi(String kaoShi) {
        this.kaoShi = kaoShi;
    }
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
   
   
    
}
