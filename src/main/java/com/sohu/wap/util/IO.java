/**
 *@version:2011-5-25-����03:26:13
 *@author:jianjunwei
 *@date:����03:26:13
 *
 */
package com.sohu.wap.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author jianjunwei
 * �ļ������࣬�����ļ�IO
 */
public class IO
{
	
	
	public static boolean deleteFile(String fileName)
	{
		File file = new File(fileName);
	   if (file.isFile())  //ɾ���ļ�
		{
		   return file.delete();
		}
	   return false;
	}

	/**
	 * 
	 *дbyte��file�� 
	 * 
	 */
	public static void writeByteToFile(ByteBuffer bytes, String destination)
	{
		try
		{
			FileChannel fc = new FileOutputStream(destination).getChannel();
		 	fc.write(bytes);
			fc.close();
			
		} catch (FileNotFoundException e)
		{
			 
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
