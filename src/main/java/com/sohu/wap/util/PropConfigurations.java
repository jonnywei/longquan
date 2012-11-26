
package com.sohu.wap.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jianjunwei
 */

public final class PropConfigurations {
    
    private static Logger  log = LoggerFactory.getLogger(PropConfigurations.class);

    private  Object  confPropertiesLock = new Object();
    
	private  Properties confProperties = new Properties();
	
	private  URL   confFileUrl;
	
	
	
	
	public PropConfigurations(String confFileName){
	    
	    URI temp = null;
	    try {
	        
	        confFileUrl = PropConfigurations.class.getClassLoader().getResource(confFileName);
	        log.info(confFileUrl.toString());
	      
	        temp =confFileUrl.toURI();
	      
	        confProperties.load(confFileUrl.openStream());
	        
        } catch (Exception e) {
        
            throw new RuntimeException("WARNING: Could not find "+confFileName+" file in class path. ");
        }
        
     
        
	}
	
	
	
	 
	 
	  /**
	     * 
	     *得到properties
	     * 
	     */
	 public  Properties  getProperties() {
	    
	      
	         synchronized(confPropertiesLock){
	             try {
	                 confProperties = new Properties();
//	                 System.out.println("dd "+confProperties.size());
                     confProperties.load(confFileUrl.openStream());
                    log.debug("getProperties大小:"+confProperties.size());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	        
	         }
	  
	     return confProperties;
	 }




    /**
     * @return the file
     */
    public File getFile() {
        try {
            return    new  File(confFileUrl.toURI());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

	
}
