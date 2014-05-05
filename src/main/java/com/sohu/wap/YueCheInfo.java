/**
 *@version:2012-5-8-下午03:03:50
 *@author:jianjunwei
 *@date:下午03:03:50
 *
 */
package com.sohu.wap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.sohu.wap.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.wap.core.Constants;
import com.sohu.wap.http.HttpUtil4Exposer;




/**
 * 用户约车(*^__^*) 嘻嘻……
 * 
 * @author jianjunwei
 *
 */
public class YueCheInfo {
    
    
    
    private static Logger  log = LoggerFactory.getLogger(YueCheInfo.class);

    private String ycDate ;

    private String carType;
  
    private    ConcurrentHashMap <Integer , YueCheItem>  yueCheInfoMap   = new ConcurrentHashMap<Integer, YueCheItem> ();
    
    
    public  YueCheInfo (String carType, String ycDate){
        this.carType = carType;
        this.ycDate = ycDate;
        init();
    }
    
    
  public ConcurrentHashMap  <Integer , YueCheItem>   getYueCheInfo(){
            return yueCheInfoMap;
    }
 
  
    /**
     * 
     * 初始化 account 对象 
     *
     */
    private  void init (){
    	
         initYueCheInfos();
    }
    
    
    private void initYueCheInfos(){
    	try {

    	 JSONArray ycArray = new JSONArray();

         String yueCheUrl = String.format(Constants.YUECHE_URL, DateUtil.getDashDate(ycDate));
    	 String  ycInfo  = HttpUtil4Exposer.getInstance().getContent(yueCheUrl);
    	 if(ycInfo != null){
    		 	ycArray  = new JSONArray(ycInfo);
			
    	 }else{
    		 log.error("get yuche info from server error");
    		 return;
    	 }
    	 for (int index =0; index < ycArray.length(); index ++){
    		
    		JSONObject yc =  ycArray.getJSONObject(index);
    		
    		YueCheItem yueCheItem = YueCheItem.jsonToYueCheItem(yc);

            String xueYuanDetailUrl =   String.format(Constants.XUEYUAN_DETAIL_URL, Integer.valueOf(yueCheItem.getXueYuanId()) );

            String  xueYuanDetailInfo  = HttpUtil4Exposer.getInstance().getContent(xueYuanDetailUrl);

             if(xueYuanDetailInfo != null){
                 JSONArray  xueYuanArray  = new JSONArray(xueYuanDetailInfo);
                 XueYuanAccount  xueYuanAccount = XueYuanAccount.jsonToXueYuanAccount(xueYuanArray.getJSONObject(0)) ;
                 yueCheItem.addXueYuanDetailInfo( xueYuanAccount);
             }
            if (carType ==null || carType.equals(yueCheItem.getCarType())){
                System.out.println(yueCheItem);
                yueCheInfoMap.put(yueCheItem.getId(), yueCheItem);
            }

    		
    	 }
    	} catch (JSONException e) {
			log.error("init yueche account from net error", e);
		}
    	 log.info("initYueCheInfos over! size=" +  yueCheInfoMap.size() );
    }
    
    
    public static void main(String[] args){
        long start = System.currentTimeMillis();

        String b=  "08:00-12:00.812.0,13:00-17:00.15.4,17:00-20:00.58.0,";

        for (int n=0; n< 1000000; n++){
            String[]  xnsds =  splitString(b,',');
            for(int i =0; i< xnsds.length; i++){
                String xnsd1 = xnsds[i];
                String[] info = splitString(xnsd1,'.');
                String sdname = info[0];
                String sdid = info[1];
                String sl = info[2];

//            System.out.println(sdname+" "+sdid+" "+sl);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);


//
        for (int n=0; n< 1000000; n++){

            String[]  xnsds =  b.split(",");
            for(int i =0; i< xnsds.length; i++){
                String xnsd1 = xnsds[i];
                String[] info = xnsd1.split("[.]");
                String sdname = info[0];
                String sdid = info[1];
                String sl = info[2];

        }
        }
        long end2 = System.currentTimeMillis();
        System.out.println(end2-end);
    }
    

        private static   String [] splitString (String str, char ch) {

            List <String> list = new ArrayList<String>();
            int length = str.length();
            int lastIndex = 0;
           for(int index =0;  index < length; index ++){
               char c = str.charAt(index);
               if(ch == c)  {
                   list.add(str.substring(lastIndex, index));
                   lastIndex = index +1;
               }
           }
           if (lastIndex !=length )  {
               list.add(str.substring(lastIndex));
           }
           String [] result = new String[list.size()];
           for (int i =0; i< result.length; i++){
               result[i] = list.get(i);
           }

            return   result ;
        }
   
    
}
