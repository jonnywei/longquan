/**
 *@version:2012-5-8-下午02:54:12
 *@author:jianjunwei
 *@date:下午02:54:12
 *
 */
package com.sohu.wap.util;

/**
 * @author jianjunwei
 *
 */
public class ThreadUtil {

    /**
     * 
     *睡眠指定的秒数
     * @throws InterruptedException 
     * 
     */
    public static  void sleep(int seconds) throws InterruptedException
    {
       
            Thread.sleep(seconds * 1000);
        
    }
    
    /**
     * 
     *睡眠指定的秒数
     * @throws InterruptedException 
     * 
     */
    public static  void sleep(float seconds) throws InterruptedException
    {
    	     float millsSeconds = seconds* 1000;
    	     long s =  (long )millsSeconds;
    	    
            Thread.sleep( s );
        
    }
    
    public static void main(String[] args){
    	 float seconds =0.0003f;
    	 float millsSeconds = seconds* 1000;
	     long s =  (long )millsSeconds;
	     System.out.println(s);
    }
     
}
