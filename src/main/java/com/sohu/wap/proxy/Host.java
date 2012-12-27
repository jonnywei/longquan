/**
 *@version:2012-12-5-下午12:21:35
 *@author:jianjunwei
 *@date:下午12:21:35
 *
 */
package com.sohu.wap.proxy;

import java.util.Date;

import org.json.JSONObject;

import com.sohu.wap.XueYuanAccount;
import com.sohu.wap.core.Constants;

/**
 * @author jianjunwei
 *
 */
public class Host {
    
    private String ip;
    private int port;
    private String type = "HTTP";
    //*NOA - non anonymous proxy, ANM - anonymous proxy server, HIA - high anonymous proxy
    private String anonymity;
    private String city;
    private String name;
    private Date checkDate;
    
    private long speed;
    
    
  public static  Host jsonToHost(JSONObject json){
        
        Host yc= new Host();
        
//        yc.setId(json.optInt("pk"));
        JSONObject field = json.optJSONObject("fields");
        yc.setIp(field.optString("ip").trim().toUpperCase());
        yc.setPort(field.optInt("port"));
//        yc.setKm(field.optString("yc_km",Constants.KM2));
//        yc.setAmPm(field.optString("yc_time"));
//        yc.setCarType(field.optString("car_type"));
//        yc.setWhiteCar(field.optString("white_car"));  
//        yc.setBlackCar(field.optString("black_car"));
//        if(field.isNull("yc_result")){
//            yc.setYcResult(BOOK_CAR_NOT_SET);
//        }else{
//            yc.setYcResult(field.optInt("yc_result"));
//        }
//        yc.setYcResultInfo(field.optString("yc_info"));
        
        return yc;
    }
    
  /**
   * @param ip
   * @param port
   */
  public Host() {
      super();
     
  }
    /**
     * @param ip
     * @param port
     */
    public Host(String ip, String port) {
        super();
        this.ip = ip;
        this.port = Integer.valueOf(port);
    }
    
    /**
     * @param ip
     * @param port
     */
    public Host(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }
    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }
    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the anonymity
     */
    public String getAnonymity() {
        return anonymity;
    }

    /**
     * @param anonymity the anonymity to set
     */
    public void setAnonymity(String anonymity) {
        this.anonymity = anonymity;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the checkDate
     */
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * @param checkDate the checkDate to set
     */
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * @return the speed
     */
    public long getSpeed() {
        return speed;
    }

    /**
     * @param time the speed to set
     */
    public void setSpeed(long time) {
        this.speed = time;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Host [anonymity=" + anonymity + ", checkDate=" + checkDate + ", city=" + city + ", ip=" + ip
                + ", name=" + name + ", port=" + port + ", speed=" + speed + ", type=" + type + "]";
    }
}
