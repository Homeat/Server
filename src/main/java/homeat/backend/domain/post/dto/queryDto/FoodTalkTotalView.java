package homeat.backend.domain.post.dto.queryDto;

import com.querydsl.core.annotations.QueryProjection;
import homeat.backend.domain.post.entity.FoodPicture;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FoodTalkTotalView {
    private Long foodTalkId;
    private String  url;
    private String foodName;
    private Integer view;
    private Integer love;

    public FoodTalkTotalView(Long foodTalkId, String url, String foodName, Integer view, Integer love) {
        this.foodTalkId = foodTalkId;
        this.url = url;
        this.foodName = foodName;
        this.view = view;
        this.love = love;
    }
}
