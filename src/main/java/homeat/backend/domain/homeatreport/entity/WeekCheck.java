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
public class WeekCheck extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_check_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // FinanceData와의 관계에서 '다'
    @JoinColumn(name = "finance_data_id")
    private FinanceData financeData;

    @ManyToOne(fetch = FetchType.LAZY) // Badge Img와의 관계에서 '다'
    @JoinColumn(name = "badge_img_id")
    private Badge_img badge_img;

    private Long goal_price; // 이번주 목표 식비

    private Long next_goal_price; // 다음주 목표 식비

    @Builder.Default
    private Long exceed_price = 0L; // 초과 금액

    // WeekStatus(enum): SUCCESS, FAIL, UNDO
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private WeekStatus weekStatus = WeekStatus.UNDO; // 이번주 달성 여부

    // TierStatus(enum): 홈잇스타터, 홈잇러버, 홈잇마스터
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TierStatus homeat_tier = TierStatus.홈잇스타터; // 홈잇 티어

    // member별 WeekCheck의 개수를 알려주는 previousWeekCheckNum
    private Long personalWeekCheckNum;

    // weekStatus setter
    public void setWeekStatus(WeekStatus weekStatus) { this.weekStatus = weekStatus; }

    // tier_status setter
    public void setTierStatus(TierStatus homeat_tier) { this.homeat_tier = homeat_tier; }

    // 다음주 목표 금액 수정
    public void updateNextGoalPrice(Long targetMoney) {
        this.next_goal_price = targetMoney;
    }

    // goal_price setter
    public void setGoalPrice(Long price) { this.goal_price = price; }

    // badge_img_id setter
    public void setBadgeImg(Badge_img badge_img) { this.badge_img = badge_img; }

    public void updateExceedPrice(Long exceed_price) { this.exceed_price = exceed_price; }

    public void updateFinanceData(FinanceData financeData) {
        this.financeData = financeData;
    }

}
