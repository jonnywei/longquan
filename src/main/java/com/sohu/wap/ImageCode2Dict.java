package com.sohu.wap;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ImageCode2的字典文件，通过文件大小和内容匹配来分析验证码
 * 
 * @author wjj
 *
 */
public class ImageCode2Dict {
	
	public static String IMAGE_CODE2_DICT_PATH="/home/wjj/i";//imagecode2
	
	public static  Map<Integer, List<ImageCodeEntry >> IMAGE_CODE2_HASH= new HashMap<Integer, List<ImageCodeEntry>>();

	
	
	private static byte []  copy(String fileFrom) {  
        try {  
            FileInputStream in = new java.io.FileInputStream(fileFrom);  
            ByteArrayOutputStream out = new ByteArrayOutputStream(10240);  
            byte[] bt = new byte[1024];  
            int count;  
            while ((count = in.read(bt)) > 0) {  
                out.write(bt, 0, count);  
            }  
          
            byte []  filebyte= out.toByteArray();
            in.close();  
            out.close(); 
            return filebyte;  
        } catch (IOException ex) {  
            return null;  
        }  
    }  
	
	public static boolean  isEquals(byte[] b1, byte[] b2){
		if(b1.length != b2.length){
			return false;
		}else{
			int count = b1.length-1;
			while (count > 0){
				if(b1[count] != b2[count]){
					return false;
				}
				count --;
			}
			
			return true;
		}
	}
  
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		File imageCode2DictDir = new File(IMAGE_CODE2_DICT_PATH);
		
		File [] codeFileList = imageCode2DictDir.listFiles();
		for (File codeFile : codeFileList){

			byte[] filebyte  = copy(codeFile.getAbsolutePath() );
			 String fileName = codeFile.getName();
			 Integer filesize = filebyte.length;
			 ImageCodeEntry imgCodeEntry = new ImageCodeEntry(fileName,filebyte);

			 List<ImageCodeEntry> fileList = IMAGE_CODE2_HASH.get(filesize);
			 if (fileList == null){
				 fileList  = new ArrayList<ImageCodeEntry> ();
				 fileList.add(imgCodeEntry);
				 IMAGE_CODE2_HASH.put(filesize, fileList);
			 }else{
				 boolean isExist =false;
				 for( ImageCodeEntry existImgCodeEntry  : fileList){
					 byte[] existFile =  existImgCodeEntry.getFileBytes();
					 if(isEquals(filebyte, existFile)){
						 isExist = true;
						 break;
					 }
				 }
				if(!isExist) {
					 fileList.add(imgCodeEntry);
				}else{
					 System.out.println( "discard  "+imgCodeEntry.getFileName());
					 codeFile.delete();
				}
			 }
			
		}
		int totalcount =0;
		Iterator<Integer> iter  = IMAGE_CODE2_HASH.keySet().iterator() ;
		while( iter.hasNext()){
			Integer key = iter.next();
			System.out.print(key+"=");
			for( ImageCodeEntry existImgCodeEntry  : IMAGE_CODE2_HASH.get(key)){
				System.out.print (existImgCodeEntry.getFileName() +" ");
				totalcount++;
			 }
			System.out.println();
		}
		System.out.println(IMAGE_CODE2_HASH.size());
		System.out.println(totalcount);

	}

	 private static class ImageCodeEntry{
		private String fileName;
		private String decodeValue;
		private byte[] fileBytes;
		ImageCodeEntry(String fileName, byte[] bytes){
			this.setFileName(fileName);
			this.fileBytes = bytes;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getFileName() {
			return fileName;
		}
		public String getDecodeValue() {
			return decodeValue;
		}
		public void setDecodeValue(String decodeValue) {
			this.decodeValue = decodeValue;
		}
		public byte[] getFileBytes() {
			return fileBytes;
		}
		public void setFileBytes(byte[] fileBytes) {
			this.fileBytes = fileBytes;
		}
		
	}
	
}
