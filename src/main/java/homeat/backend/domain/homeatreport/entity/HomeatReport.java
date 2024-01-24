package homeat.backend.domain.homeatreport.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import homeat.backend.domain.user.entity.Member;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeatReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homeat_report_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long total_success;

    @OneToMany(mappedBy = "homeatReport", cascade = CascadeType.ALL) // CascadeType을 ALL로 설정해두면 HomeatReport를 persist할 때 Week의 객체들도 같이 persist됨.
    private List<Week> weeks = new ArrayList<>();

    // 양방향 연관관계 편의 메소드
}
