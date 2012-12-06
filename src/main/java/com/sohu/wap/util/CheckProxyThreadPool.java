package com.sohu.wap.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckProxyThreadPool {
    
    private static int nThreads = 10;
    
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    private static CheckProxyThreadPool _instance = null;

    public static CheckProxyThreadPool getInstance() {
        if (_instance == null) {
            synchronized (CheckProxyThreadPool.class) {
                if (_instance == null) {
                    _instance = new CheckProxyThreadPool();
                }
            }
        }
        return _instance;
    }

    
    
    public ExecutorService getExecutorService() {
        return executorService;
    }

    private CheckProxyThreadPool() {

    }
}
