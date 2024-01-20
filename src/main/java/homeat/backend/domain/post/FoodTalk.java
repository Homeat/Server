package homeat.backend.domain.post;

import homeat.backend.domain.common.BaseEntity;
import homeat.backend.user.domain.Member;
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
public class FoodTalk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodtalk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;
    private String ingredient;
    private String memo;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    private Integer love;
    private Integer view;
    private Integer commentNumber;

    @Enumerated(EnumType.STRING)
    private Save save;

    public void update(String name, String ingredient, String memo, Tag tag) {
        this.name = name;
        this.ingredient = ingredient;
        this.memo = memo;
        this.tag = tag;
    }


}
