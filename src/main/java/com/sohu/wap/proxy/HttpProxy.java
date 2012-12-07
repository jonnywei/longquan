/**
 * 
 */
package com.sohu.wap.proxy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wjj
 *
 */
public interface HttpProxy {
	
	public  ConcurrentHashMap  <String, Host>   getProxy();
	
	public  Host  getRandomHost();
}
