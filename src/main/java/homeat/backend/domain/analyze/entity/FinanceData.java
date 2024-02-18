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

    // 회원가입 시 1회 + 매달 1일마다 1회 + (InActive -> Active 마다 1회 생성)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finance_data_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    private Long month_jipbap_price = 0L; // 이번달 집밥 비용

    @Builder.Default
    private Long month_out_price = 0L; // 이번달 배달/외식 비용

    @Builder.Default
    private Long num_homeat_badge = 0L; // 홈잇뱃지 개수

    public void addJipbapPrice(long price) {
        this.month_jipbap_price += price;
    }

    public void addOutPrice(long price) {
        this.month_out_price += price;
    }

    public void setNumHomeatBadge(Long num_badge) { this.num_homeat_badge = num_badge; }
}
