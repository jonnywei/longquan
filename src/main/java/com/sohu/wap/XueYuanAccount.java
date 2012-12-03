/**
 *@version:2012-11-26-下午06:35:49
 *@author:jianjunwei
 *@date:下午06:35:49
 *
 */
package com.sohu.wap;

/**
 * @author jianjunwei
 *
 */
public class XueYuanAccount {
    private int id;
    private String userName;
    private String password;
    
    private String carType;
    
    private String yueCheDate;
    
    private String amPm;
    
    private String whiteCar;
    
    
    private boolean isBookSuccess = false;
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
}
