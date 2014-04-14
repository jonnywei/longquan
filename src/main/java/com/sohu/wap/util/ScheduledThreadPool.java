package com.sohu.wap.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledThreadPool {
    
    private static int corePoolSize = 20;
    
    private ScheduledExecutorService  executorService = Executors.newScheduledThreadPool(corePoolSize);

    private static ScheduledThreadPool _instance = null;

    public static ScheduledThreadPool getInstance() {
        if (_instance == null) {
            synchronized (ScheduledThreadPool.class) {
                if (_instance == null) {
                    _instance = new ScheduledThreadPool();
                }
            }
        }
        return _instance;
    }
    
    public ScheduledExecutorService getScheduledExecutorService() {
        return executorService;
    }

    private ScheduledThreadPool() {

    }
}
