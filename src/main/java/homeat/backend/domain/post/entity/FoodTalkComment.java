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
public class FoodTalkComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodtalk_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodtalk_id")
    private FoodTalk foodTalk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Builder.Default
    private Integer reportNumber = 0;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "foodTalkComment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FoodTalkReply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "foodTalkComment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FoodTalkCommentReport> foodTalkCommentReports = new ArrayList<>();

    public void reported() {
        this.status = Status.신고;
    }

    public void plusReport(int nowReport) {
        this.reportNumber = nowReport;
    }
}
