package org.ha.ckh637.component;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class DataCenter {
    private DataCenter(){}
    private final Map<String, PromoForm> keyPromoFormMap = new LinkedHashMap<>();
    private final Map<String, PromoForm> keyUrgentServiceFormMap = new LinkedHashMap<>();
    private final Map<String, CachedData> batchCachedDataMap = new HashMap<>();
    private String cachedUrgSerSpeEmailHTML = "";
    private static final DataCenter dataCenter = new DataCenter();
    public static DataCenter getInstance(){return dataCenter;}

    public void addPromoForm(String key_ITOCMS, PromoForm promoForm){
        keyPromoFormMap.put(key_ITOCMS, promoForm);
    }

    public void addUrgentServiceForm(String key_ITOCMS, PromoForm promoForm){
        keyUrgentServiceFormMap.put(key_ITOCMS, promoForm);
    }

    public PromoForm getPromoFormByKey_ITOCMS(String key_ITOCMS){
        return keyPromoFormMap.get(key_ITOCMS);
    }

    public String getCachedUrgSerSpeEmailHTML(){
        return cachedUrgSerSpeEmailHTML;
    }

    public void setCachedUrgSerSpeEmailHTML(String cachedUrgSerSpeEmailHTML){
        this.cachedUrgSerSpeEmailHTML = cachedUrgSerSpeEmailHTML;
    }

    public void resetCachedUrgSerSpeEmailHTML(){
        cachedUrgSerSpeEmailHTML = "";
    }

    public PromoForm getUrgentServiceFormByKey_ITOCMS(String key_ITOCMS){
        return keyUrgentServiceFormMap.get(key_ITOCMS);
    }

    public void clearAllApiData(){
        keyPromoFormMap.clear();
        keyUrgentServiceFormMap.clear();
    }

}
