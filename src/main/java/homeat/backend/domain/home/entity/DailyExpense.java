package homeat.backend.domain.home.entity;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyExpense extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_expense_id")
    private Long id;

    // FinanceData 와의 관계에서 '다'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finance_data_id")
    private FinanceData financeData;

    // 오늘 집밥 비용
    @Builder.Default
    private long todayJipbapPrice = 0;

    // 오늘 외식(배달 포함) 비용
    @Builder.Default
    private long todayOutPrice = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}

