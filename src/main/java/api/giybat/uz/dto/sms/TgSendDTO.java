package api.giybat.uz.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class TgSendDTO {
    private String phone_number ;
     private String code_length;

    public TgSendDTO(String phone_number) {
        this.phone_number = phone_number;
        this.code_length = "5";
    }
}
