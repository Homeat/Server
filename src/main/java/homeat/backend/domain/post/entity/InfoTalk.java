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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InfoTalk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "infotalk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private String content;


    @Builder.Default
    private Integer love = 0;
    @Builder.Default
    private Integer view = 0;
    @Builder.Default
    private Integer commentNumber = 0;
    @Builder.Default
    private Boolean setLove = false;

    @Enumerated(EnumType.STRING)
    private Save save;

    @OneToMany(mappedBy = "infoTalk", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InfoPicture> infoPictures = new ArrayList<>();

    @OneToMany(mappedBy = "infoTalk", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InfoHashTag> infoHashTags = new ArrayList<>();

    @OneToMany(mappedBy = "infoTalk", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InfoTalkComment> infoTalkComments = new ArrayList<>();

    public void update(String title, String content) {

        this.title = title;
        this.content = content;
    }

    public void updateCommentSize(int nowNum) {
        this.commentNumber = nowNum;
    }

    public void plusView(int nowView) {
        this.view = nowView;
    }

    public void setLove(boolean nowState) {
        this.setLove = nowState;
    }

    public void plusLove(int nowLove) {
        this.love = nowLove;
    }
}
