package org.ha.ckh637.service;


import org.ha.ckh637.component.RequestData;
import org.ha.ckh637.config.SingletonConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class DirectoryService {
    private DirectoryService(){}
//    private static final PromoReleaseEmailConfig PROMO_RELEASE_EMAIL_CONFIG = PromoReleaseEmailConfig.getInstance();

    public static String getTempSrcDirectory(final String year_batch) {
        String tempSrcDirectory = SingletonConfig.getIniInputPath() + "\\" + year_batch;
        createDir(tempSrcDirectory);
        return tempSrcDirectory;
    }

    public static String getTempDestDirectory() {
        String tempDestDirectory = SingletonConfig.getIniInputPath() + "\\tempDestDir";
        createDir(tempDestDirectory);
        return tempDestDirectory;
    }

    private static void createDir(String inputPath){
        Path srcPath = Paths.get(inputPath);
        if (!Files.exists(srcPath)){
            try {
                Files.createDirectories(srcPath);
            } catch (Exception e) {
                System.out.println("Failed to create tempSrcDir directory.\n");
            }
        }
    }

    public static void delTempDestDirectory(){
        String tempDestDirectory = SingletonConfig.getIniInputPath() + "\\tempDestDir";
        deleteDirectory(new File(tempDestDirectory));
    }

    public static void delDir(final String year_batch){
        String tempSrcDirectory = SingletonConfig.getIniInputPath() + "\\" + year_batch;
//        String tempDestDirectory = SingletonConfig.getIniInputPath() + "\\tempDestDir";
        deleteDirectory(new File(tempSrcDirectory));
//        deleteDirectory(new File(tempDestDirectory));
    }

    private static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

}
