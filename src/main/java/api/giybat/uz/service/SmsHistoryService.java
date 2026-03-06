package api.giybat.uz.service;

import api.giybat.uz.entity.SmsHistoryEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.SmsHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsHistoryService {
    @Value("${spring.limit.time}")
    private Integer timeLimit;
    @Value("${spring.limit.attempt}")
    private Integer attemptCount;
    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleService bundleService;

    public void create(String phone, String message, String code, SmsType type) {

        SmsHistoryEntity smsHistory = new SmsHistoryEntity();
        smsHistory.setPhone(phone);
        smsHistory.setMessage(message);
        smsHistory.setCode(code);
        smsHistory.setAttemptCount(0);
        smsHistory.setSmsType(type);
        smsHistory.setCreatedDate(LocalDateTime.now());


        smsHistoryRepository.save(smsHistory);
    }

    public Long getSmsCount(String phone) {
        LocalDateTime now = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phone, now.minusMinutes(2), now);
    }

    public void check(String phone, String code, AppLanguage language) {

        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phone);
        if (optional.isEmpty()) {
            log.warn("Verification failed: phone {}, code {} " , phone, code);
            throw new AppBadException(bundleService.getMessage("verification.failed", language));
        }
        SmsHistoryEntity entity = optional.get();

        if (entity.getAttemptCount()>=attemptCount){
            log.warn("Code limit failed:  phone {}, code {} " , phone, code);
            throw new AppBadException(bundleService.getMessage("code.limit.failed", language));
        }

        if (!code.equals(entity.getCode())) {
            smsHistoryRepository.updateAttemptCount(entity.getId());
            log.warn("Verification code invalid: phone {}, code {} " , phone, code);
            throw new AppBadException(bundleService.getMessage("verification.code.invalid", language));
        }

        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(timeLimit);
        if (LocalDateTime.now().isAfter(expDate)) {
            log.warn("Verification code date invalid: phone {}, code {} " , phone, code);
            throw new AppBadException(bundleService.getMessage("verification.code.date.invalid", language));
        }
    }


}
