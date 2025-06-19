package api.giybat.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsResendDTO {
    @NotBlank(message = "Phone required")
    private String phone;
}
