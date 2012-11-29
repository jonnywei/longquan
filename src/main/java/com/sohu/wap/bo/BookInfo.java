/**
 *@version:2012-11-29-下午07:03:12
 *@author:jianjunwei
 *@date:下午07:03:12
 *
 */
package com.sohu.wap.bo;

import java.util.Date;

/**
 * @author jianjunwei
 *
 */
public class BookInfo {

    private int id;
    
    private int  yueCheInfoId;
    
    private String idNum;
    
    private String date;
    
    private String amPm;
    
    private String carInfo;
    
    private Date  operationDate;
    
    private String extend;

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
     * @return the yueCheInfoId
     */
    public int getYueCheInfoId() {
        return yueCheInfoId;
    }

    /**
     * @param yueCheInfoId the yueCheInfoId to set
     */
    public void setYueCheInfoId(int yueCheInfoId) {
        this.yueCheInfoId = yueCheInfoId;
    }

    /**
     * @return the idNum
     */
    public String getIdNum() {
        return idNum;
    }

    /**
     * @param idNum the idNum to set
     */
    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

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
     * @return the carInfo
     */
    public String getCarInfo() {
        return carInfo;
    }

    /**
     * @param carInfo the carInfo to set
     */
    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }

    /**
     * @return the operationDate
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * @param operationDate the operationDate to set
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * @return the extend
     */
    public String getExtend() {
        return extend;
    }

    /**
     * @param extend the extend to set
     */
    public void setExtend(String extend) {
        this.extend = extend;
    }
    
    
}
