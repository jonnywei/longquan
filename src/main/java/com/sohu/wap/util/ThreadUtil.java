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
     * 
     */
    public static  void sleep(int seconds)
    {
        try
        {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e)
        {
        
            e.printStackTrace();
        }
    }
}
