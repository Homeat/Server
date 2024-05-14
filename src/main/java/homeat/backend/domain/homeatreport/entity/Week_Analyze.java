package homeat.backend.domain.homeatreport.entity;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Week_Analyze extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_analyze_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // FinanceData와의 관계에서 '다'
    @JoinColumn(name = "finance_data_id")
    private FinanceData financeData;

    @Builder.Default
    private Long week_jipbap_price = 0L; // n째주 집밥 비용

    @Builder.Default
    private Long week_out_price = 0L; // n째주 배달외식 가격

    @Builder.Default
    private Integer weekZeroExpense = 1; // 이번주 지출 여부. 1이면 이번주 지출이 없음, 0이면 이번주 지출이 있음

    // week_jipbap_price setter
    public void setJipbapPrice(Long price) { this.week_jipbap_price = price; }

    // week_out_price setter
    public void setOutPrice(Long price) { this.week_out_price = price; }

    // FinaceData updater
    public void updateFinanceData(FinanceData financeData) {
        this.financeData = financeData;
    }

}
