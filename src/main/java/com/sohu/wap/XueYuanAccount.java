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
}
