package api.giybat.uz;

import api.giybat.uz.enums.SmsType;
import api.giybat.uz.service.SmsSendService;
import api.giybat.uz.service.SmsTelegramSendService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsTelegramSendService smsTelegramSendService;

    @Test
    void contextLoads() {
        System.out.println(smsSendService.sendSms("+998337706673","Это тест от Eskiz","34342", SmsType.REGISTRATION));
    }

}