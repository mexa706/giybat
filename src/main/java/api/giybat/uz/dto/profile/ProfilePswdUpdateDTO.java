package api.giybat.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfilePswdUpdateDTO {
    @NotBlank(message = "old password required")
    private String oldPassword;
    @NotBlank(message = "new password required")
    private String newPassword;
}
