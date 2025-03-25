package org.ha.ckh637.service;

import org.ha.ckh637.component.CachedData;
import org.ha.ckh637.component.EmailHTML;
import org.ha.ckh637.component.RequestData;
import org.ha.ckh637.component.DataCenter;
import org.ha.ckh637.utils.JsonDataParser;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class PayloadHandler {
    private PayloadHandler(){}
    private static final DataCenter DATA_CENTER = DataCenter.getInstance();
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = ConcurencyControl.getREAD_LOCK();
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = ConcurencyControl.getWRITE_LOCK();

    public static byte[] handleGetZipByBatch(String payload){
        if (isValidYearBatch(payload)){
            String[] yearBatch = splitYearAndBatch(payload);
            if (isNumeric(yearBatch[0]) && isNumeric(yearBatch[1])){
                String year = yearBatch[0];
                String batch = yearBatch[1].length() == 1 ? "0" + yearBatch[1] : yearBatch[1];
                String year_batch = year + "-" + batch;
                try{
                    READ_LOCK.lock();
                    Map<String, CachedData> cachedDataMap = DATA_CENTER.getBatchCachedDataMap();
                    if (cachedDataMap.containsKey(year_batch)){
                        CachedData cachedData = cachedDataMap.get(year_batch);
                        byte[] zipFileByte = cachedData.getAttachment();
                        if (zipFileByte != null){
                            return zipFileByte;
                        }
                    }else{
                        READ_LOCK.unlock();
                        try{
                            WRITE_LOCK.lock();
                            RequestData requestData = new RequestData(year, batch);
                            eventSequenceBiweekly_V2_multiThreaded(requestData, year_batch);
                        }finally {
                            WRITE_LOCK.unlock();
                        }
                        READ_LOCK.lock();
                        if (cachedDataMap.containsKey(year_batch)){
                            CachedData cachedData = cachedDataMap.get(year_batch);
                            byte[] zipFileByte = cachedData.getAttachment();
                            if (zipFileByte != null){
                                return zipFileByte;
                            }
                        }
                    }
                }finally {
                    READ_LOCK.unlock();
                }
            }
        }
        return null;
    }

    public static Map<String, String> handleReceiveUrgSerSpeEmail(final String email_address){
        try{
            if (isInvalidEmail(email_address)) {
                return Map.of("status", "error",
                        "message", "\"" + email_address + "\"" + "is an invalid email address.");
            }
            try{
                READ_LOCK.lock();
                String cachedUrgSerSpeEmailHTML = DATA_CENTER.getCachedUrgSerSpeEmailHTML();
                if (!cachedUrgSerSpeEmailHTML.isBlank()){
                    EmailService.sendUrgentServiceEmail_V2(email_address, cachedUrgSerSpeEmailHTML);
                    return Map.of("status", "success",
                            "message", "Success! Please check your email inbox.");
                }else{
                    try{
                        READ_LOCK.unlock();
                        WRITE_LOCK.lock();
                        cachedUrgSerSpeEmailHTML = DATA_CENTER.getCachedUrgSerSpeEmailHTML();
                        if (!cachedUrgSerSpeEmailHTML.isBlank()){
                            EmailService.sendUrgentServiceEmail_V2(email_address, cachedUrgSerSpeEmailHTML);
                            return Map.of("status", "success",
                                    "message", "Success! Please check your email inbox.");
                        }
                        eventSequenceUrgSerSpe();
                    }finally {
                        WRITE_LOCK.unlock();
                    }
                    READ_LOCK.lock();
                    cachedUrgSerSpeEmailHTML = DATA_CENTER.getCachedUrgSerSpeEmailHTML();
                    if (!cachedUrgSerSpeEmailHTML.isBlank()){
                        EmailService.sendUrgentServiceEmail_V2(email_address, cachedUrgSerSpeEmailHTML);
                        return Map.of("status", "success",
                                "message", "Success! Please check your email inbox.");
                    }
                    return Map.of("status", "error",
                            "message", "Failed to send email. Please try again later.");
                }
            }finally {
                READ_LOCK.unlock();
            }
        }catch (Exception e){
            return Map.of("status", "error",
                    "message", "Exception encountered in the backend: " + e.getMessage());
        }
    }

    public static Map<String, String> handleReceiveBiweeklyEmail(String email_address, String year_batch){
        try{
            if (isInvalidEmail(email_address)) {
                return Map.of("status", "error",
                        "message", "\"" + email_address + "\"" + "is an invalid email address.");
            }
            if(isValidYearBatch(year_batch)){
                String[] yearBatch = splitYearAndBatch(year_batch);
                if (isNumeric(yearBatch[0]) && isNumeric(yearBatch[1])){
                    String year = yearBatch[0];
                    String batch = yearBatch[1].length() == 1 ? "0" + yearBatch[1] : yearBatch[1];
                    year_batch = year + "-" + batch;
                    try{
                        READ_LOCK.lock();
                        Map<String, CachedData> cachedDataMap = DATA_CENTER.getBatchCachedDataMap();
                        if (cachedDataMap.containsKey(year_batch)){
                            EmailService.sendBiweeklyEmailWithAttachment(email_address, year_batch, cachedDataMap.get(year_batch));
                            return Map.of("status", "success",
                                    "message", "Success! Please check your email inbox.");
                        }else{
                            READ_LOCK.unlock();
                            try{
                                WRITE_LOCK.lock();
                                if (cachedDataMap.containsKey(year_batch)){
                                    EmailService.sendBiweeklyEmailWithAttachment(email_address, year_batch, cachedDataMap.get(year_batch));
                                    return Map.of("status", "success",
                                            "message", "Success! Please check your email inbox.");
                                }
                                RequestData requestData = new RequestData(year, batch);
                                eventSequenceBiweekly_V2_multiThreaded(requestData, year_batch);
//                            return Map.of("status", "success",
//                                    "message", "Success! Please check your email inbox.");
                            }finally {
                                WRITE_LOCK.unlock();
                            }
                            READ_LOCK.lock();
                            if (cachedDataMap.containsKey(year_batch)){
                                EmailService.sendBiweeklyEmailWithAttachment(email_address, year_batch, cachedDataMap.get(year_batch));
                                return Map.of("status", "success",
                                        "message", "Success! Please check your email inbox.");
                            }
                        }
                    }finally {
                        READ_LOCK.unlock();
                    }
                }
                return Map.of("status", "error",
                        "message", "Payload year-batch is of incorrect format.");
            }
            return Map.of("status", "error",
                    "message", "Payload year-batch is of incorrect format.");
        } catch (Exception e) {
            return Map.of("status", "error",
                    "message", "Exception encountered in the backend: " + e.getMessage());
        }
    }

    private static boolean isInvalidEmail(String email){
        return !(email.contains("@") && email.contains("."));
    }

    private static boolean isValidYearBatch(String year_batch){
        return year_batch.contains("-");
    }

    private static String[] splitYearAndBatch(String year_batch){
        return year_batch.split("-");
    }

    private static boolean isNumeric(String input){
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void eventSequenceUrgSerSpe(){
        DATA_CENTER.clearAllApiData();
        Thread t1 = new Thread(() -> {
            Mono<String> monoJiraResp = APIQueryService.fetchJiraUrgentServiceAPI_V2();
            JsonDataParser.parseStandardJiraResp(null, monoJiraResp, false);
        });
        t1.start();
        try{
            t1.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String finalContent = EmailHTML.compileEmailHTMLContent(true);
        DATA_CENTER.setCachedUrgSerSpeEmailHTML(finalContent);
        DATA_CENTER.clearAllApiData();
        new BackgroundService().scheduleDeleteUrgSerSpeCachedData();
    }

    private static void eventSequenceBiweekly_V2_multiThreaded(RequestData requestData, final String year_batch) {
        DATA_CENTER.clearAllApiData();
        Thread t1 = new Thread(() -> {
            Mono<String> monoJiraResp = APIQueryService.fetchJiraBiweeklyAPI_V2(requestData);
            JsonDataParser.parseStandardJiraResp(year_batch, monoJiraResp, true);
        });
        Thread t2 = new Thread(() -> {
            Mono<String> monoJiraResp = APIQueryService.fetchJiraUrgentServiceForBiweeklyAPI_V2(requestData);
            JsonDataParser.parseJiraUrgentServiceForBiweeklyResp(monoJiraResp);
        });
        t1.start(); t2.start();
        try {
            t1.join(); t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t3 = new Thread(() -> ReadmeService.createReadmeFile(year_batch, ReadmeService.genReadmeContent()));
        Thread t4 = new Thread(()->{
            String part1 = CompileSeqService.compilePart1Forwarder();
            String part2 = CompileSeqService.compilePart2BackendScript();
            String part3 = CompileSeqService.compilePart3UrgentServicePromotion();
            CompileSeqService.createCompileSeqFile(year_batch, CompileSeqService.genCompileSeqContent(year_batch, part1, part2, part3));
        });
        Thread t5 = new Thread(() -> UrlService.genAllUrlFiles_V2(year_batch));
        t3.start(); t4.start(); t5.start();
        try {
            t3.join(); t4.join(); t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ZipService.compressFileToZip(year_batch);
//        EmailService.sendBiweeklyEmailWithAttachment_V2();
        String emailContent = EmailHTML.compileEmailHTMLContent(false);
        String targetZip = ZipService.getZipFilePath(year_batch);
        byte[] zipFileByte;
        try{
            zipFileByte = Files.readAllBytes(Paths.get(targetZip));
        }catch (IOException e){
            zipFileByte = null;
        }
        DATA_CENTER.getBatchCachedDataMap().put(year_batch, new CachedData(emailContent, targetZip, zipFileByte));
        DATA_CENTER.clearAllApiData();
        DirectoryService.delDir(year_batch);
        new BackgroundService().scheduleDeleteBiweeklyCachedData(year_batch);
    }
}
