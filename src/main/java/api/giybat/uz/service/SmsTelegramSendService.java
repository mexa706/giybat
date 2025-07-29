package api.giybat.uz.service;

import api.giybat.uz.dto.sms.TgSendDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class SmsTelegramSendService  {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${telegram.url}")
    private String tgUrl;
    @Value("${telegram.token}")
    private String token;

    protected HttpEntity<String> makePayload(HashMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);


        ObjectMapper mapper = new ObjectMapper();
        String data = null;
        try {
            data = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

         return new HttpEntity<>(data, headers);
    }

    public String checkAbility(String phone) {
        HashMap<String, String> body = new HashMap<>();
        body.put("phone_number", phone);

        HttpEntity<String> payload = this.makePayload(body);

        ResponseEntity<String> response = restTemplate.exchange(tgUrl + "/checkSendAbility",
                HttpMethod.POST,
                payload,
                String.class
        );

        JsonNode root;
        try {
            ObjectMapper res = new ObjectMapper();
            root = res.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (! root.get("ok").asBoolean()) {
            throw new RuntimeException("Error sending SMS: " + response.getBody());
        }

        JsonNode result = root.get("result");

        if (result == null || ! result.has("request_id")) {
            throw new RuntimeException("Error sending SMS: " + response.getBody());
        }

        return result.get("request_id").asText();
    }

    public String sendVerificationCode(String phone) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        TgSendDTO body = new TgSendDTO(phone);

        HttpEntity<TgSendDTO> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(tgUrl + "/sendVerificationMessage",
                HttpMethod.POST,
                entity,
                String.class);



        try {
            JsonNode data = new ObjectMapper().readTree(response.getBody());


            JsonNode ok = data.get("ok");
            if (!ok.asBoolean()){
                throw new RuntimeException("Error sending verification Message: " + data.get("error").asText());
            }


            JsonNode result = data.get("result");
            JsonNode deliveryStatus = result.get("delivery_status");
            JsonNode status = deliveryStatus.get("status");
            return status.asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public String checkVerificationCode(String phone, String code, String requestId) {

        HashMap<String, String> body = new HashMap<>();

        body.put("phone_number", phone);
        body.put("request_id", requestId);
        body.put("code", code);


        HttpEntity<String> payload = this.makePayload(body);

        ResponseEntity<String> response = restTemplate.exchange(tgUrl + "/checkVerificationStatus",
                HttpMethod.POST,
                payload,
                String.class
        );

        JsonNode root;
        try {
            ObjectMapper res = new ObjectMapper();
            root = res.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (! root.get("ok").asBoolean()) {
            throw new RuntimeException("Error sending SMS: " + response.getBody());
        }


        JsonNode result = root.get("result");

        JsonNode verification_status = result.get("verification_status");

        JsonNode status = verification_status.get("status");

        return status.asText();

    }

}
