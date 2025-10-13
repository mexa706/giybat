package api.giybat.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileUsernameUpdateDTO {
    @NotBlank(message = "username required")
    private String username;
}
