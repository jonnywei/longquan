
package com.sohu.wap.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统配置信息，不需要动态读取
 * @author jianjunwei
 */

public final class SystemConfigurations {
    
    private static Logger  log = LoggerFactory.getLogger(SystemConfigurations.class);

 	private static Properties system = new Properties();

	
	public static String getSystemStringProperty(String key, String defaultValue) {
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return system.getProperty(key);
	}

	public static int getSystemIntProperty(String key, int defaultValue) {
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Integer.parseInt(system.getProperty(key));
	}
	
	public static short getSystemShortProperty(String key, short defaultValue) {
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Short.parseShort(system.getProperty(key));
	}

	public static long getLongProperty(String key, long defaultValue) {
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return Long.parseLong(system.getProperty(key));
	}
	
	public static boolean getSystemBooleanProperty(String key, boolean defaultValue) {
		if (system == null || system.getProperty(key) == null) {
			return defaultValue;
		}
		return system.getProperty(key).toLowerCase().trim().equals("true");
	}
	

	 

	static {
	  
		try {
			system.load(SystemConfigurations.class.getClassLoader().getResourceAsStream("system.properties"));
		} catch (Exception e) {
			system = null;
			System.err.println("WARNING: Could not find system.properties  file in class path.");
			 
		}
		
	}
}
