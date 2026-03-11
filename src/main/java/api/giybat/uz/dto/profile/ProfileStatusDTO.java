package api.giybat.uz.dto.profile;

import api.giybat.uz.enums.GeneralStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileStatusDTO {
    @NotNull(message = "status required")
    private GeneralStatus status;
}
