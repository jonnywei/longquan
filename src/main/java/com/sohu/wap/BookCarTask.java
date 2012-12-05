/**
 *@version:2012-12-5-下午07:05:17
 *@author:jianjunwei
 *@date:下午07:05:17
 *
 */
package com.sohu.wap;

import java.util.concurrent.Callable;

import org.json.JSONObject;

import com.sohu.wap.service.ProxyBookCar;

/**
 * @author jianjunwei
 *
 */
public class BookCarTask implements Callable<Integer> {

    private JSONObject carInfo;
    
    private JSONObject cookieInfo;
    

    /**
     * @param carInfo
     * @param cookieInfo
     */
    public BookCarTask(JSONObject carInfo, JSONObject cookieInfo) {
        super();
        this.carInfo = carInfo;
        this.cookieInfo = cookieInfo;
    }
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception {
        
        ProxyBookCar.book(carInfo, cookieInfo);
        // TODO Auto-generated method stub
        return null;
    }



}
