package homeat.backend.domain.home.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receipt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_expense_id")
    private DailyExpense dailyExpense;

    @Enumerated(EnumType.STRING)
    private CostType costType;

    private Long expense;

    @Column(columnDefinition = "TEXT" /*, nullable = false */)
    private String memo;
}