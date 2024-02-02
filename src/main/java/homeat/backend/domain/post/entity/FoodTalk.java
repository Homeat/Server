package homeat.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import homeat.backend.global.common.domain.BaseEntity;
import homeat.backend.domain.user.entity.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FoodTalk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodtalk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;
    private String memo;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Builder.Default
    private Integer love = 0;

    @Builder.Default
    private Integer view = 0;

    @Builder.Default
    private Integer commentNumber = 0;

    @Enumerated(EnumType.STRING)
    private Save save;

    @OneToMany(mappedBy = "foodTalk", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FoodPicture> foodPictures = new ArrayList<>();

    public void update(String name, String memo, Tag tag) {
        this.name = name;
        this.memo = memo;
        this.tag = tag;
    }

    public void plusView(int nowView) {
        this.view = nowView;
    }


}
