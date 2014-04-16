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
import com.sohu.wap.util.NetChangedReloadingStrategy;



/**
 * 用户约车(*^__^*) 嘻嘻……
 * 
 * @author jianjunwei
 *
 */
public class ScanYueCheInfo {
    
    
    
    private static Logger  log = LoggerFactory.getLogger(ScanYueCheInfo.class);
    
    private    ConcurrentHashMap <String , XueYuanAccount>  yueCheInfoMap   = new ConcurrentHashMap<String, XueYuanAccount> ();
    
    private  static  NetChangedReloadingStrategy strategy  = new NetChangedReloadingStrategy();

    public  ScanYueCheInfo (){
    	reload();
    }
    
    
  public ConcurrentHashMap  <String , XueYuanAccount>   getYueCheInfo(){
	  		reload();
            return yueCheInfoMap;
    }
 
  
    /**
     * 
     * 初始化 account 对象 
     *
     */
    private  synchronized void reload (){
    	 if (strategy.reloadingRequired()){
             loadScanYueCheInfos();

    	 }
    }
    
    private  synchronized  void loadScanYueCheInfos(){
    	try {
    	 JSONArray ycArray = new JSONArray(); 
    	 String  ycInfo  = HttpUtil4Exposer.getInstance().getContent(Constants.YUECHE_SCAN_URL);
    	 if(ycInfo != null){
    		 	ycArray  = new JSONArray(ycInfo);
			
    	 }else{
    		 log.error("get yuche info from server error");
    		 return;
    	 }
    	 
    	 
    	 for (int index =0; index < ycArray.length(); index ++){
    		
    		JSONObject yc =  ycArray.getJSONObject(index);
    		
    		XueYuanAccount xyAccount = XueYuanAccount.jsonToXueYuanAccount(yc);
//    		3=330211199308031530;0803;20140418@am,pm|20140419@am,pm;pm;byd;km2
    		String fstr = xyAccount.getYueCheDate()+"@"+xyAccount.getAmPm();
    		if(yueCheInfoMap.containsKey( xyAccount.getUserName()) ){
    			XueYuanAccount old = yueCheInfoMap.get(xyAccount.getUserName());
    			old.setYueCheDate(old.getYueCheDate()+"|"+fstr);
    			
    		}else{
    			xyAccount.setYueCheDate(fstr);
    	 		yueCheInfoMap.put(xyAccount.getUserName() , xyAccount);
    		}
    		
    	 }
    	 

    	} catch (JSONException e) {
			log.error("init yueche account from net error", e);
		}
    	 log.info("initScanYueCheInfos over! size=" +  yueCheInfoMap.size() );
    }
    
    
    
    public static void main(String [] args) {
    	ScanYueCheInfo  s = new ScanYueCheInfo();
    	
    }

    
   
    
}
