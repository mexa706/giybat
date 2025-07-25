package api.giybat.uz.service;

import api.giybat.uz.entity.EmailHistoryEntity;
import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.EmailType;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exps.AppBadExeptions;

import api.giybat.uz.repository.EmailHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailHistoryService {
    @Value("${spring.limit.time}")
    private Integer timeLimit;
    @Value("${spring.limit.attempt}")
    private Integer attemptCount;
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBundleService bundleService;

    public void create(String email, String message, String code, EmailType type) {

        EmailHistoryEntity emailHistory = new EmailHistoryEntity();
        emailHistory.setEmail(email);
        emailHistory.setMessage(message);
        emailHistory.setCode(code);
        emailHistory.setAttemptCount(0);
        emailHistory.setEmailType(type);
        emailHistory.setCreatedDate(LocalDateTime.now());


        emailHistoryRepository.save(emailHistory);
    }

    public Long getEmailCount(String email) {
        LocalDateTime now = LocalDateTime.now();
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(2), now);
    }

    public void check(String email, String code, AppLanguage language) {

        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()) {
            throw new AppBadExeptions(bundleService.getMessage("verification.failed", language));
        }
        EmailHistoryEntity entity = optional.get();

        if (entity.getAttemptCount()>=attemptCount) {
            throw new AppBadExeptions(bundleService.getMessage("code.limit.failed", language));
        }

        if (!code.equals(entity.getCode())) {
           emailHistoryRepository.updateAttemptCount(entity.getId());
            throw new AppBadExeptions(bundleService.getMessage("verification.code.invalid", language));
        }

        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(timeLimit);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new AppBadExeptions(bundleService.getMessage("verification.code.date.invalid", language));
        }
    }


}
