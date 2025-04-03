package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.PhantomReference;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {
    private String username;
    private Integer id;
    private List<ProfileRole> roleList;
}
