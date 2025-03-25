package org.ha.ckh637.service;


import org.ha.ckh637.component.RequestData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipService {
//    private static final PromoReleaseEmailConfig PROMO_RELEASE_EMAIL_CONFIG = PromoReleaseEmailConfig.getInstance();
    private ZipService(){}

    public static String getZipFilePath(final String year_batch){
        return DirectoryService.getTempDestDirectory() + "\\" + year_batch + ".zip";
    }

    public static void compressFileToZip(String year_batch) {
//        String srcDir = DirectoryService.getTempSrcDirectory(year_batch);
//        String destDir = DirectoryService.getTempDestDirectory();
//        String zipFileName = year_batch + ".zip";
        try {
//            WRITE_LOCK.lock();
            File srcFile = new File(DirectoryService.getTempSrcDirectory(year_batch));
            FileOutputStream fos = new FileOutputStream(getZipFilePath(year_batch));
            ZipOutputStream zos = new ZipOutputStream(fos);
            addDirToArchive(zos, srcFile, srcFile.getName());
            zos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            WRITE_LOCK.unlock();
        }
    }

    private static void addDirToArchive(ZipOutputStream zos, File srcFile, String basePath) throws IOException {
        File[] files = srcFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirToArchive(zos, file, basePath + File.separator + file.getName());
                } else {
                    addToArchive(zos, file, basePath + File.separator + file.getName());
                }
            }
        }
    }

    private static void addToArchive(ZipOutputStream zos, File file, String entryName) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(entryName);
        zos.putNextEntry(zipEntry);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }
        zos.closeEntry();
        fis.close();
    }
}
