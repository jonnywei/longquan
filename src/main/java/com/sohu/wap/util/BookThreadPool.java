package com.sohu.wap.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookThreadPool {
    
    private static int nThreads = 200;
    
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    private static BookThreadPool _instance = null;

    public static BookThreadPool getInstance() {
        if (_instance == null) {
            synchronized (BookThreadPool.class) {
                if (_instance == null) {
                    _instance = new BookThreadPool();
                }
            }
        }
        return _instance;
    }

    
    
    public ExecutorService getExecutorService() {
        return executorService;
    }

    private BookThreadPool() {

    }
}
