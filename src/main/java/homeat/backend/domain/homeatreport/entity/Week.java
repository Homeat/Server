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
public class Week extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // FinanceData와의 관계에서 '다'
    @JoinColumn(name = "finance_data_id") // FK 이름이 finance_data_id
    private FinanceData financeData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_img_id")
    private Badge_img badge_img;

    private Long goal_price; // 이번주 목표 식비

    // WeekStatus(enum) : SUCCESS, FAIL, UNDO
    @Enumerated(EnumType.STRING)
    private WeekStatus week_status; // 이번주 달성 여부

    private Long exceed_price; // 초과 금액

}
