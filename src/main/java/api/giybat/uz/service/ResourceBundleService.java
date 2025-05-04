package api.giybat.uz.service;

import api.giybat.uz.enums.AppLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ResourceBundleService {
    @Autowired
    private ResourceBundleMessageSource BundleMessage;




    public String getMessage(String code, AppLanguage language) {
        return         BundleMessage.getMessage(code,null,new Locale(language.name()));

    }
}
