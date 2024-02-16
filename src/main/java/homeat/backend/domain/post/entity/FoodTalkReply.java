package homeat.backend.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import homeat.backend.domain.user.entity.Member;
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
public class FoodTalkReply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodtalk_reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodtalk_comment_id")
    @JsonIgnore
    private FoodTalkComment foodTalkComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    private String content;
}
