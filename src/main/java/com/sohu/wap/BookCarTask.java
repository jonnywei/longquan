/**
 *@version:2012-12-5-下午07:05:17
 *@author:jianjunwei
 *@date:下午07:05:17
 *
 */
package com.sohu.wap;

import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.service.ProxyBookCar;
import com.sohu.wap.util.RandomUtil;

/**
 * @author jianjunwei
 *
// */
//public class BookCarTask extends YueChe2  implements Callable<Integer> {
//
//	private static Logger log = LoggerFactory.getLogger(BookCarTask.class);
//	
//    private JSONArray carArrayInfo;
//    
//    private JSONObject cookieInfo;
//    
//
//    /**
//     * @param carInfo
//     * @param cookieInfo
//     */
//    public BookCarTask(JSONArray carArrayInfo, JSONObject cookieInfo) {
//        super();
//        this.carArrayInfo = carArrayInfo;
//        this.cookieInfo = cookieInfo;
//    }
//    
//    
//    /* (non-Javadoc)
//     * @see java.util.concurrent.Callable#call()
//     */
//    @Override
//    public Integer call() throws Exception {
//    	if (carArrayInfo.length() == 0) {
//    		return  BookCarUtil.NO_CAR;
//		}
//    	JSONObject selectedCar = carArrayInfo.getJSONObject(RandomUtil.getRandomInt(carArrayInfo.length()));
//
//		if (selectedCar == null) {
//			return  BookCarUtil.SELECT_CAR_ERROR;
//		}
//		log.info("选择的车是：" + selectedCar.toString());
//		
//		JSONObject bookCarJson = getBookCarJson(selectedCar, getImgCode() ,getHiddenKM(false));
//	
//		JSONObject result =  ProxyBookCar.book(bookCarJson, cookieInfo);
//		
//    	return BookCarUtil.bookResult(result);
//    }
//
//    
//
//}
