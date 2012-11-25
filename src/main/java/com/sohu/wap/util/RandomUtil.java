package com.sohu.wap.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

 

public class RandomUtil {
    
    
    
    public static String getJSRandomDecimals() {
        StringBuffer sb = new StringBuffer();
        sb.append("0.").append(getRandomString(16));
        return sb.toString();
    }
    
    public static String getRandomString(int size) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 获取随机rnum值
     * @return
     */
    public static String getRandomRnum(){
    	 return MD5.crypt(System.currentTimeMillis() + new Random().nextInt(10000) + "");
    }
    
    /**
     * 获取白名单随机的rnum值
     * @return
     */
    public static String getRandomBairnum(){
        return MD5.crypt(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 
     * 返回 小于< maxNum 的随机数字
     * @author jianjunwei  2010-07-05
     * @return  0<=xxx<maxNum
     * 
     */
    public static int getRandomInt(int maxNum)
    {
    	if(maxNum <= 0)
    		return 0;
    	Random random = new Random();
    	int rnum = random.nextInt(10000);
    	return rnum %maxNum;
    }
    public static void main(String[] args) {
      for (int i =0; i < 1000; i ++)
    	  System.out.println(RandomUtil.getRandomInt(0));

    }

	/**
	 * 获取一个无重复的随机正整数集合
	 * @param max 最大值
	 * @param size 集合大小
	 * @return
	 */
	public static Set<Integer> getNoDuplicateRandomInt (int max, int size) throws Exception{
		Set<Integer> indexSet = new HashSet<Integer>();
		if(size > 0){
			if (max > size) {
				Random random = new Random();
				while (indexSet.size() < size) {
					indexSet.add(random.nextInt(max));
				}
			} else if (max == size) {
				for(int i = 0; i < size; i++){
					indexSet.add(i);
				}
			}else{
				throw new Exception("获取无重复随机正整数集合出错：获取整数的最大值不能小于获取的数量！");
			}
		}
		
		return indexSet;
	}
	/**
	 * 圣诞活动获取随机码
	 * @return
	 */
	public static String genDecorationCode() {
		long time = System.currentTimeMillis();
		String decorationCode = "j" + time;
		Random r = new Random();
		decorationCode += (100000000 + r.nextInt(1000000000));// 随机数
		String md5 = MD5.crypt(decorationCode + "sohuzzx2011110913681125987");
		md5 = md5.substring(md5.length() - 10);
		decorationCode = md5+decorationCode;
		return decorationCode;
	}
	 
}
