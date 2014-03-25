package com.sohu.wap.proxy;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.sohu.wap.util.ThreadUtil;

public class ConfigProxyTest {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ConcurrentHashMap  <String, Host>  hostMap = ConfigHttpProxy.getInstance().getProxy();
		System.out.println(ConfigHttpProxy.getInstance().getRandomHost());
//		System.exit(0);
		ThreadUtil.sleep(180);
		Iterator<String> iter = hostMap.keySet().iterator();
		int index =0;
		while(iter.hasNext()){
			index ++;
			String key = iter.next();
			Host host =hostMap.get(key);
			System.out.println(index+"="+host.getIp()+":"+host.getPort());
		}
		System.exit(1);
	}

}
