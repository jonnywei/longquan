
package com.sohu.wap.util;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;

/**
 * 系统配置信息，不需要动态读取
 * @author jianjunwei
 */

public final class NetSystemConfigurations {
    
    private static Logger  log = LoggerFactory.getLogger(NetSystemConfigurations.class);
    
    private  static  NetChangedReloadingStrategy strategy  = new NetChangedReloadingStrategy();

 	private static Properties system = new Properties();

 	
 	
	
	public static String getSystemStringProperty(String key, String defaultValue) {
	    reload();
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return system.getProperty(key);
	}

	public static int getSystemIntProperty(String key, int defaultValue) {
	    reload();
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Integer.parseInt(system.getProperty(key));
	}
	
	public static short getSystemShortProperty(String key, short defaultValue) {
	    reload();
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Short.parseShort(system.getProperty(key));
	}

	public static long getLongProperty(String key, long defaultValue) {
	    reload();
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Long.parseLong(system.getProperty(key));
	}
	
	public static boolean getSystemBooleanProperty(String key, boolean defaultValue) {
	    reload();
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return system.getProperty(key).toLowerCase().trim().equals("true");
	}
	

	 
	
	
	
    //初始化加载
	static {
	  
	    reload();
		log.info("NetSystemConfigurations init over!");
	}
	
	
	
	//网络设置覆盖本地设置
	
	private  static  void reload(){
	    try {
	    	
	    	
            
	          if (strategy.reloadingRequired()){
	                 
	        	   system.load(NetSystemConfigurations.class.getClassLoader().getResourceAsStream("system.properties"));
	                
	        	   boolean useNetAdmin =  SystemConfigurations.getSystemBooleanProperty(Constants.CFG_SYSTEM_USE_NET_ADMIN, false);
	    		   if(useNetAdmin){
	    			   String sp = HttpUtil4Exposer.getInstance().getContent(Constants.CONFIG_URL);

		                if (sp == null){
		                    log.error("load config from net eror");
		                    sp="";
		                }
	    			   StringReader  sr = new StringReader(sp); 
	    			   system.load(sr);
	    		   }else{
	    			   log.info("log config from net disable");
	    		   }
	              
	          }
	           
	        } catch (Exception e) {
	            log.error("load error",e);
	            system = null;
	            System.err.println("ERROR:load erorr.");
	             
	        }
	}
	
	
	
	public static void main(String [] args) throws InterruptedException{
	    while(true){
	        Iterator iter =  system.keySet().iterator();
	        while(iter.hasNext()){
	            String key = (String) iter.next();
	            System.out.println(key +"="+ getSystemStringProperty(key,"aa"))  ;
	        }
	        ThreadUtil.sleep(60);
	    }
	  
	 
	}
	
}
