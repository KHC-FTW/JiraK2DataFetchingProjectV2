package org.ha.ckh637.service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.ha.ckh637.component.CachedData;
import org.ha.ckh637.component.DataCenter;
import org.ha.ckh637.component.EmailHTML;
import org.ha.ckh637.component.PromoForm;
import org.ha.ckh637.config.SingletonConfig;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EmailService {
    private EmailService(){}
    private static final DataCenter DATA_CENTER = DataCenter.getInstance();
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = ConcurencyControl.getREAD_LOCK();

    public static boolean sendBiweeklyEmailWithAttachment(final String email_address, final String year_batch, final CachedData cachedData){
        try{
            Dispatch mail = Dispatch
                    .invoke(new ActiveXComponent("Outlook.Application"),
                            "CreateItem",
                            Dispatch.Get,
                            new Object[]{"0"},
                            new int[0])
                    .toDispatch();

            SingletonConfig singletonConfig = SingletonConfig.getInstance();
            Dispatch.put(mail, "Subject", singletonConfig.getEmailSubjectBiweekly(year_batch));
            Dispatch.put(mail, "To", email_address);
            String emailContent = cachedData.getEmailContent();
            String attachmentPath = cachedData.getAttachmentPath();
            Dispatch.put(mail, "HTMLBody", emailContent);
            // Attach a document
            Dispatch attachments = Dispatch.get(mail, "Attachments").toDispatch();
            Dispatch.call(attachments, "Add", attachmentPath);
            // Set reminder properties
            Dispatch.put(mail, "ReminderSet", false);
            Dispatch.call(mail, "Send");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean sendUrgentServiceEmail_V2(final String email_address, final String cachedUrgSerSpeEmailHTML){
        try{
            Dispatch mail = Dispatch
                    .invoke(new ActiveXComponent("Outlook.Application"),
                            "CreateItem",
                            Dispatch.Get,
                            new Object[]{"0"},
                            new int[0])
                    .toDispatch();

            SingletonConfig singletonConfig = SingletonConfig.getInstance();
            Dispatch.put(mail, "Subject", singletonConfig.getEmailSubjectUrgentService());
            Dispatch.put(mail, "To", email_address);

            ////////////////////////////////////////////
            Dispatch.put(mail, "HTMLBody", cachedUrgSerSpeEmailHTML);
            //////////////////////////////////////////

            // Set reminder properties
            Dispatch.put(mail, "ReminderSet", false);
            Dispatch.call(mail, "Send");
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
