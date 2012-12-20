/**
 *@version:2012-5-8-下午03:03:50
 *@author:jianjunwei
 *@date:下午03:03:50
 *
 */
package com.sohu.wap;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;




/**
 * 用户约车(*^__^*) 嘻嘻……
 * 
 * @author jianjunwei
 *
 */
public class YueCheInfo {
    
    
    
    private static Logger  log = LoggerFactory.getLogger(YueCheInfo.class);
    
  
    private    ConcurrentHashMap <Integer , XueYuanAccount>  yueCheInfoMap   = new ConcurrentHashMap<Integer, XueYuanAccount> ();
    
    
    public  YueCheInfo (){
        init();
    }
    
    
  public ConcurrentHashMap  <Integer , XueYuanAccount>   getYueCheInfo(){
            return yueCheInfoMap;
    }
 
  
    /**
     * 
     * 初始化 account 对象 
     *
     */
    private  void init (){
    	
         initYueCheInfos();
    }
    
    
    private void initYueCheInfos(){
    	try {
    	 JSONArray ycArray = new JSONArray();;
    	 String  ycInfo  = HttpUtil4Exposer.getInstance().getContent(Constants.YUECHE_URL);
    	 if(ycInfo != null){
    		 	ycArray  = new JSONArray(ycInfo);
			
    	 }else{
    		 log.error("get yuche info from server error");
    		 return;
    	 }
    	 for (int index =0; index < ycArray.length(); index ++){
    		
    		JSONObject yc =  ycArray.getJSONObject(index);
    		
    		XueYuanAccount xyAccount = XueYuanAccount.jsonToXueYuanAccount(yc);
    		
    		yueCheInfoMap.put(xyAccount.getId(), xyAccount);
    		
    	 }
    	} catch (JSONException e) {
			log.error("init yueche account from net error", e);
		}
    	 log.info("initXuYuanAccounts over!" );
    }
    
    
    
    

    
   
    
}
