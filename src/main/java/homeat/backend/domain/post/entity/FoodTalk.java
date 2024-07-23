package homeat.backend.domain.post.entity;

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
    private String ingredient;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Builder.Default
    private Integer love = 0;

    @Builder.Default
    private Integer view = 0;

    @Builder.Default
    private Integer commentNumber = 0;

    @Builder.Default
    private Integer reportNumber = 0;

    @Builder.Default
    private Boolean setLove = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "foodTalk", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FoodRecipe> foodRecipes = new ArrayList<>();

    public void update(String name, String memo, Tag tag) {

        this.name = name;
        this.memo = memo;
        this.tag = tag;
    }

    public void updateCommentSize(int nowSize) {
        this.commentNumber = nowSize;
    }

    public void plusLove(int nowLove) {
        this.love = nowLove;
    }

    public void setLove(boolean nowState) {
        this.setLove = nowState;
    }

    public void reported() {
        this.status = Status.신고;
    }

    public void plusReport(int nowReport) {
        this.reportNumber = nowReport;
    }


}
