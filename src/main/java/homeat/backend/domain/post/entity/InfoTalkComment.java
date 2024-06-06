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
public class InfoTalkComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "infotalk_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infotalk_id")
    private InfoTalk infoTalk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;

    @Builder.Default
    private Integer reportNumber = 0;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "infoTalkComment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InfoTalkReply> replyList = new ArrayList<>();

    @OneToMany(mappedBy = "infoTalkComment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InfoTalkCommentReport> infoTalkCommentReports = new ArrayList<>();

    public void reported() {
        this.status = Status.신고;
    }

    public void plusReport(int nowReport) {
        this.reportNumber = nowReport;
    }
}
