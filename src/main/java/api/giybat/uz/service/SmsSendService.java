package api.giybat.uz.service;

import api.giybat.uz.dto.sms.SmsAuthDTO;
import api.giybat.uz.dto.sms.SmsAuthResponseDTO;
import api.giybat.uz.dto.sms.SmsRequestDTO;
import api.giybat.uz.dto.sms.SmsSendResponseDTO;
import api.giybat.uz.entity.SmsProviderTokenHolder;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.SmsType;
import api.giybat.uz.exps.AppBadExeptions;
import api.giybat.uz.repository.SmsProviderTokenRepository;
import api.giybat.uz.util.RandomUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import org.flywaydb.core.internal.util.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsSendService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${eskiz.url}")
    private String smsUrl;
    @Value("${eskiz.login}")
    private String login;
    @Value("${eskiz.password}")
    private String password;
    @Value("${spring.limit.attempt}")
    private Integer attemptCount;
    @Autowired
    private SmsProviderTokenRepository smsProviderTokenRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBundleService bundleService;
    public void sendRegistrationSms(String phone, AppLanguage language) {
        String code = RandomUtil.getRandomSmsCode();

        /* String message = "%s";
        message = String.format(message, phone);*/

        System.out.println("Registaration code : "+code);
        String testMessage =bundleService.getMessage("registration.confirm.code", language);

        sendSms(phone, testMessage, code, SmsType.REGISTRATION,language);
    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message, String code, SmsType smsType, AppLanguage language) {

        Long countSms = smsHistoryService.getSmsCount(phoneNumber);
        if (countSms >= attemptCount) {
            throw new AppBadExeptions(bundleService.getMessage("sms.limit.reached", language));
        }

        SmsSendResponseDTO result = sendSms(phoneNumber, message);

        smsHistoryService.create(phoneNumber, message, code, smsType);

        return result;
    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {

        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        SmsRequestDTO body = new SmsRequestDTO();
        body.setMobile_phone(phoneNumber);
        body.setFrom("4546");
        body.setMessage(message);


        HttpEntity<SmsRequestDTO> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(smsUrl + "/message/sms/send",
                    HttpMethod.POST,
                    entity,
                    SmsSendResponseDTO.class);


            return response.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        Optional<SmsProviderTokenHolder> optional = smsProviderTokenRepository.findTop1By();
        String token = getTokenFromProvider();
        if (optional.isEmpty()) {
            SmsProviderTokenHolder entity = new SmsProviderTokenHolder();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenRepository.save(entity);
            return entity.getToken();
        }

        SmsProviderTokenHolder entity = optional.get();

        if (LocalDateTime.now().isBefore(entity.getExpiredDate())) {
            return entity.getToken();
        }
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
        smsProviderTokenRepository.save(entity);


        return token;
    }

    private String getTokenFromProvider() {

        SmsAuthDTO smsAuthDto = new SmsAuthDTO(login, password);
        String response = restTemplate.postForObject(smsUrl + "/auth/login", smsAuthDto, String.class);
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            JsonNode data = jsonNode.get("data");
            String token = data.get("token").asText();
            return token;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);

        }

        /*  return restTemplate.postForObject(smsUrl + "/auth/login"
                , smsAuthDto
                , SmsAuthResponseDTO.class).getData().getToken();*/

    }

    public void sendResetPasswordSms(String username, AppLanguage language) {

        String code = RandomUtil.getRandomSmsCode();

        /* String message = "%s";
        message = String.format(message, phone);*/

        System.out.println("reset password code : "+code);
        String testMessage =bundleService.getMessage("registration.confirm.code", language);

        sendSms(username, testMessage, code, SmsType.RESET_PASSWORD,language);


    }
}

