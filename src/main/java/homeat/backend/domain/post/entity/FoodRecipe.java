package homeat.backend.domain.post.entity;

import homeat.backend.global.common.domain.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodRecipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_recipe_id")
    private Long id;

    private String recipe;
    private String ingredient;
    private String tip;
    private Integer step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodtalk_id")
    private FoodTalk foodTalk;
}
