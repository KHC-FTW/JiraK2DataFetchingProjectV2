package org.ha.ckh637.service;


import org.ha.ckh637.component.DataCenter;
import org.ha.ckh637.component.PromoForm;
import org.ha.ckh637.component.RequestData;

import java.io.FileWriter;

public class UrlService {
    private static final DataCenter DATA_CENTER = DataCenter.getInstance();

    private static void genNormalBiweeklyUrlFiles(final String year_batch){
        String sourceDirectory = DirectoryService.getTempSrcDirectory(year_batch);
        for(PromoForm promoForm: DATA_CENTER.getKeyPromoFormMap().values()){
            if(promoForm.isImpHospOrImpCorp()){
                String finalFileName = "";
                String status = promoForm.getStatus();
                if (promoForm.getK2FormLink().isBlank()) continue;
                final String targetK2Url = promoForm.getK2FormLink() + "&tab=PRD";
                if(status.equalsIgnoreCase("Withdrawn") || status.equalsIgnoreCase("Rejected")){
                    finalFileName += String.format("[%s] ", status);
                }
                finalFileName += promoForm.getConcatenatedReadmePromoName();
                String concatenatedRelationshipString = promoForm.getConcatenatedRelationshipString();
                if(!concatenatedRelationshipString.isBlank()){
                    finalFileName += String.format(" (%s)", concatenatedRelationshipString);
                }
                finalFileName = filterFileName(finalFileName) + ".url";
                try {
                    FileWriter writer = new FileWriter(sourceDirectory + "\\" + finalFileName);
                    writer.write("[InternetShortcut]\n");
                    writer.write("URL=" + targetK2Url + "\n");
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void genUrgentServiceBiweeklyUrlFiles(String year_batch){
        String sourceDirectory = DirectoryService.getTempSrcDirectory(year_batch);
        for(PromoForm promoForm: DATA_CENTER.getKeyUrgentServiceFormMap().values()){
            final String targetK2Url = promoForm.getK2FormLink() + "&tab=PRD";
            String finalFileName = promoForm.getConcatenatedReadmePromoName();
            String concatenatedRelationshipString = promoForm.getConcatenatedRelationshipString();
            if(!concatenatedRelationshipString.isBlank()){
                finalFileName += String.format(" (%s)", concatenatedRelationshipString);
            }
            finalFileName = filterFileName(finalFileName) + ".url";
            try {
                FileWriter writer = new FileWriter(sourceDirectory + "\\" + finalFileName);
                writer.write("[InternetShortcut]\n");
                writer.write("URL=" + targetK2Url + "\n");
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String filterFileName(String fileName){
        return fileName.replaceAll("[<>]", "");
    }

    public static void genAllUrlFiles_V2(final String year_batch){
        genNormalBiweeklyUrlFiles(year_batch);
        genUrgentServiceBiweeklyUrlFiles(year_batch);
    }

    /*public static void genUrlFile(){
        String sourceDirectory = DirectoryService.getTempSrcDirectory();
        for (ReadmeItemPPM item: TEXT_SUMMARY.getAllReadmeItemPPM()) {
            String status = item.getStatus();
            if(status.equalsIgnoreCase("Withdrawn") || status.equalsIgnoreCase("Rejected")){
                continue;
            }
            String allTypes = item.getAllTypes();
            if (allTypes.contains("imp-hosp") || allTypes.contains("imp-corp")) {
                final String targetK2Url = String.format("https://wfeng-svc/Runtime/Runtime/Form/CMS__Promotion__Form/?formNumber=%s&tab=PRD", item.getK2FormNo());
                final String linkedIssues = item.getLinkedIssues();
                final String fileName = item.getPromotion() + (linkedIssues.isBlank() ? "" : String.format(" (%s)", linkedIssues)) + ".url";
                try {
                    FileWriter writer = new FileWriter(sourceDirectory + "\\" + fileName);
                    writer.write("[InternetShortcut]\n");
                    writer.write("URL=" + targetK2Url + "\n");
                    writer.close();
                } catch (Exception e) {
                    System.out.printf("An error occurred while creating %s.%n", fileName);
                }
            }
        }
        for (ReadmeItemUrgentService item: TEXT_SUMMARY.getAllReadmeItemUrgentServices()) {
            final String targetK2Url = String.format("https://wfeng-svc/Runtime/Runtime/Form/CMS__Promotion__Form/?formNumber=%s&tab=PRD", item.getK2FormNo());
            final String linkedIssues = item.getLinkedIssues();
            final String fileName = item.getPromotion() + (linkedIssues.isBlank() ? "" : String.format(" (%s)", linkedIssues)) + ".url";
            try {
                FileWriter writer = new FileWriter(sourceDirectory + "\\" + fileName);
                writer.write("[InternetShortcut]\n");
                writer.write("URL=" + targetK2Url + "\n");
                writer.close();
            } catch (Exception e) {
                System.out.printf("An error occurred while creating %s.%n", fileName);
            }
        }
    }*/
}
