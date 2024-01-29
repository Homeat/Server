package homeat.backend.domain.post.dto.queryDto;

import com.querydsl.core.annotations.QueryProjection;
import homeat.backend.domain.post.entity.FoodPicture;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FoodTalkResponse {

    private String name;
    private String memo;
    private Integer love;
    private Integer view;
    private Integer commentNumber;
    private String url;

    @QueryProjection
    public FoodTalkResponse(String name, String memo, Integer love, Integer view, Integer commentNumber,
                            String url) {
        this.name = name;
        this.memo = memo;
        this.love = love;
        this.view = view;
        this.commentNumber = commentNumber;
        this.url = url;
    }
}
