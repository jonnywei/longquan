/**
 *@version:2012-11-30-下午05:14:10
 *@author:jianjunwei
 *@date:下午05:14:10
 *
 */
package com.sohu.wap.bo;

import org.json.JSONObject;

/**
 * @author jianjunwei
 *
 */
public class Result<T extends Object> {
    private T data;
    private int ret;
    private Object extend;
    private JSONObject json;
    
    public Result(int ret1){
        this.ret = ret1;
    }
    /**
     * @return the data
     */
    public T getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
    /**
     * @return the ret
     */
    public int getRet() {
        return ret;
    }
    /**
     * @param ret the ret to set
     */
    public void setRet(int ret) {
        this.ret = ret;
    }
    /**
     * @return the extend
     */
    public Object getExtend() {
        return extend;
    }
    /**
     * @param extend the extend to set
     */
    public void setExtend(Object extend) {
        this.extend = extend;
    }
    /**
     * @return the json
     */
    public JSONObject getJson() {
        return json;
    }
    /**
     * @param json the json to set
     */
    public void setJson(JSONObject json) {
        this.json = json;
    }
    
}
