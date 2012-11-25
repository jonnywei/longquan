package com.sohu.wap.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Util 
{
	private static Logger log = LoggerFactory.getLogger(Util.class);
	private Util(){}
	
	public static String getLargePictureAddress(String pictureUrl)
	{
		String lagrePictureUrl =pictureUrl.replace("thumbnail", "large");
		return lagrePictureUrl;
	}
	
	public static Date dateAddSecond(Date date, int second)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, second);
		return cal.getTime();
		
	}
	
	public static String encode(String m)
	{
		String result = null;
		try {
			result = URLEncoder.encode(m,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String encodeUTF8(String m)
	{
		String result = null;
		try {
			result = URLEncoder.encode(m,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public static String decode(String m)
	{
		String result = null;
		try {
			result = URLDecoder.decode(m,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		return result;
	}
	
   /**
    * 
    *����΢����ʱ�䣬��֤��ȷ 
    * 
    */
	public static Date getDateFromString(String m)
	{
		Date result = null;
		 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				result = sdf.parse(m);
			} catch (ParseException e)
			{
				// ������ڽ���ʧ�ܣ����ն����ڷ���
				SimpleDateFormat sdfshort = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					result = sdfshort.parse(m);
				} catch (ParseException e1)
				{
					// ��Ȼʧ�ܵĻ�,���յ�ǰʱ����������
					result = new Date();
					log.info(m+"����ʧ�ܣ����յ�ǰʱ���������á�");
					e1.printStackTrace();
				}
			}
		return result;
	}
	
	
	public static String generateUUID(){
        String str = UUID.randomUUID().toString();
        return str.replaceAll("-", "");
		    
	}
	
	
	public static Date getEndOfHour(){
	    return getEndOfHour(null, Calendar.getInstance()); 
	}
	
	
	private static Date getEndOfHour(Date day, Calendar cal) {
        if (day == null)
            day = new Date();
        cal.setTime(day);
//        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        return cal.getTime();
    }
	

	public static Date getFutureHour(int hour) {
	    if (hour < 0){
	        hour =0;
	    }
	    Calendar cal = Calendar.getInstance();
	    Date    day = new Date();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTime().before(day)){
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY)+24);
        }
        return cal.getTime();
    }
	
}
