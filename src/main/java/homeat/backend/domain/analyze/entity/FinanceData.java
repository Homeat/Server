package homeat.backend.domain.analyze.entity;

import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FinanceData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finance_data_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long month_jipbap_price; // 이번달 집밥 비용

    private Long month_out_price; // 이번달 배달/외식 비용

    private Long income; // 사용자 수입

    private Long num_homeat_badge; // 홈잇뱃지 개수

}
