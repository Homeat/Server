package homeat.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import homeat.backend.global.common.domain.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodtalk_id")
    @JsonIgnore
    private FoodTalk foodTalk;

    @OneToMany(mappedBy = "foodRecipe", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FoodRecipePicture> foodRecipePictures = new ArrayList<>();
}
