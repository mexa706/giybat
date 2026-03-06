package api.giybat.uz.dto.post;

import api.giybat.uz.dto.attach.AttachCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class PostCreateDTO {
    @NotBlank(message = "title required")
    @Length(min = 1, max = 255 , message="min=5 , max=255")
    private String title;
    @NotBlank(message = "content required")
    private String content;
    @NotNull(message = "attachId required")
    private AttachCreateDTO photo;


}
