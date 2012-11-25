package com.sohu.wap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 
 * 
 *从 blog2-comon-0.1.1.jar  中copy 过来的 com.sohu.blog.core.util.MD5
 * 
 * @author jianjunwei
 * 
 */
public class MD5
{

    public MD5()
    {
    }

    public static String crypt(String str)
    {
        if(str == null || str.length() == 0)
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte hash[] = md.digest();
            for(int i = 0; i < hash.length; i++)
                if((0xff & hash[i]) < 16)
                    hexString.append((new StringBuilder("0")).append(Integer.toHexString(0xff & hash[i])).toString());
                else
                    hexString.append(Integer.toHexString(0xff & hash[i]));

        }
        catch(NoSuchAlgorithmException e)
        {
            return "";
        }
        return hexString.toString();
    }
}