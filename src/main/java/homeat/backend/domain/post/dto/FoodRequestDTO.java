package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Tag;
import java.util.List;
import javax.mail.Multipart;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class FoodRequestDTO {

    @Getter
    public static class CommentDTO {
        Long id;
        String content;
    }



}
