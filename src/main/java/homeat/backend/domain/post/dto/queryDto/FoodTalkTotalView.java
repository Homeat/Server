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

    public FoodTalkTotalView(Long foodTalkId, String url, String foodName) {
        this.foodTalkId = foodTalkId;
        this.url = url;
        this.foodName = foodName;
    }
}
