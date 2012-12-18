/**
 *@version:2012-5-22-下午03:27:02
 *@author:jianjunwei
 *@date:下午03:27:02
 *
 */
package com.sohu.wap.util;

/**
 * @author jianjunwei
 *
 */


import java.net.URI;

public class NetChangedReloadingStrategy  
{
     

    /** Constant for the default refresh delay.*/
    private static final int DEFAULT_REFRESH_DELAY = 60000;

    /** Stores a reference to the configuration to be monitored.*/
    protected URI  monitoredFileURI;

    /** The last time the configuration file was modified. */
    protected long lastModified;

    /** The last time the file was checked for changes. */
    protected long lastChecked;

    /** The minimum delay in milliseconds between checks. */
    protected long refreshDelay = DEFAULT_REFRESH_DELAY;

   

    public boolean reloadingRequired()
    {
        boolean reloading = false;

        long now = System.currentTimeMillis();

        if (now > lastChecked + refreshDelay)
        {
            lastChecked = now;
            if (hasChanged())
            {
                reloading = true;
            }
        }

        return reloading;
    }

   

    /**
     * Return the minimal time in milliseconds between two reloadings.
     *
     * @return the refresh delay (in milliseconds)
     */
    public long getRefreshDelay()
    {
        return refreshDelay;
    }

    /**
     * Set the minimal time between two reloadings.
     *
     * @param refreshDelay refresh delay in milliseconds
     */
    public void setRefreshDelay(long refreshDelay)
    {
        this.refreshDelay = refreshDelay;
    }

    
    /**
     * Check if the configuration has changed since the last time it was loaded.
     *
     * @return a flag whether the configuration has changed
     */
    protected boolean hasChanged()
    {
        
        return true;
    }

  

    /**
     * @return the monitoredFileURI
     */
    public URI getMonitoredFileURI() {
        return monitoredFileURI;
    }

    /**
     * @param monitoredFileURI the monitoredFileURI to set
     */
    public void setMonitoredFileURI(URI monitoredFileURI) {
        this.monitoredFileURI = monitoredFileURI;
    }

   

    
}


