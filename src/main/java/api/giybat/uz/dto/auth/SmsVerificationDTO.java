package api.giybat.uz.dto.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SmsVerificationDTO {
    @NotBlank(message = "phone required")
    private String phone;
    @NotBlank(message = "code required")
    private String code;

}
