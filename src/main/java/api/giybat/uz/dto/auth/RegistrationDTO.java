package api.giybat.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {
    @NotBlank(message = "username required")
    private String username;
    @NotBlank(message = "password required")
    private String password;
    @NotBlank(message = "name required")
    private String name;
}
