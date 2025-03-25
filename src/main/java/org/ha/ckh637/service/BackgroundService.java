package org.ha.ckh637.service;

import org.ha.ckh637.component.DataCenter;
import org.ha.ckh637.config.SingletonConfig;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BackgroundService {
    private final ScheduledExecutorService scheduler;
    private static final DataCenter DATA_CENTER = DataCenter.getInstance();
    private static final SingletonConfig SINGLETON_CONFIG = SingletonConfig.getInstance();
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = ConcurencyControl.getWRITE_LOCK();
    public BackgroundService(){
        scheduler = Executors.newScheduledThreadPool(1);
//        System.out.println("BackgroundService() constructor called.");
    }
    public void scheduleDeleteBiweeklyCachedData(final String year_batch) {
//        System.out.printf("Waiting %d minutes before deleting cached zip file for %s\n", SINGLETON_CONFIG.getAdminCacheTimeMin(), year_batch);
        scheduler.schedule(() -> {
            String targetZip = ZipService.getZipFilePath(year_batch);
            File targetFile = new File(targetZip);
            try{
                WRITE_LOCK.lock();
                if (targetFile.exists()) {
                    targetFile.delete();
//                    System.out.println("Deleted cached zip file for " + year_batch);
                }
                DATA_CENTER.getBatchCachedDataMap().remove(year_batch);
//                System.out.println("Removed cached data for " + year_batch);
            }finally {
                WRITE_LOCK.unlock();
//                System.out.printf("BackgroundService scheduler shutdown - %s.\n", year_batch);
                this.scheduler.shutdown();
            }
        }, SINGLETON_CONFIG.getAdminCacheTimeMin(), TimeUnit.MINUTES);
    }

    public void scheduleDeleteUrgSerSpeCachedData() {
        scheduler.schedule(() -> {
            try{
                WRITE_LOCK.lock();
                DATA_CENTER.resetCachedUrgSerSpeEmailHTML();
            }finally {
                WRITE_LOCK.unlock();
                this.scheduler.shutdown();
            }
        }, SINGLETON_CONFIG.getAdminCacheTimeMin(), TimeUnit.MINUTES);
    }
}
