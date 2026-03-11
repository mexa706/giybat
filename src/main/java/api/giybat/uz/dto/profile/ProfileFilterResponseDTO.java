package api.giybat.uz.dto.profile;

import api.giybat.uz.dto.attach.AttachDTO;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProfileFilterResponseDTO {
    private Integer id;
    private String name;
    private String username;
    private LocalDateTime createdDate;
    private GeneralStatus status;
    private Integer postCount;
    private List<ProfileRole> roles;
    private AttachDTO photo;
}
