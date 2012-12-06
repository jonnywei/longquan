package com.sohu.wap.proxy;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.sohu.wap.util.ThreadUtil;

public class ConfigProxyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConcurrentHashMap  <String, Host>  hostMap = ConfigProxy.getInstance().getProxy();
		ThreadUtil.sleep(60);
		Iterator<String> iter = hostMap.keySet().iterator();
		int index =0;
		while(iter.hasNext()){
			index ++;
			String key = iter.next();
			Host host =hostMap.get(key);
			System.out.println(index+"="+host.getIp()+":"+host.getPort());
		}
	}

}
