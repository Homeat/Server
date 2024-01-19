package homeat.backend.user.domain;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImgUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LoginType loginType = LoginType.DEFAULT;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "member_status")
    private MemberStatus status = MemberStatus.ACTIVE;
}