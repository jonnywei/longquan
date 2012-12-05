package com.sohu.wap.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    
    private static int nThreads = 100;
    
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    private static ThreadPool _instance = null;

    public static ThreadPool getInstance() {
        if (_instance == null) {
            synchronized (ThreadPool.class) {
                if (_instance == null) {
                    _instance = new ThreadPool();
                }
            }
        }
        return _instance;
    }

    
    
    public ExecutorService getExecutorService() {
        return executorService;
    }

    private ThreadPool() {

    }
}
