package homeat.backend.domain.homeatcash.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Week extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "week_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // HomeatCash와의 관계에서 '다'
    @JoinColumn(name = "homeat_cash_id") // FK 이름이 homeatCash_id
    private HomeatCash homeatCash;

    // WeekStatus(enum) : SUCCESS, FAIL, UNDO
    @Enumerated(EnumType.STRING)
    private WeekStatus week_status;

    @OneToOne(mappedBy = "week", fetch = FetchType.LAZY)
    private BadgePage_txt badgePageTxt;

    @OneToOne(mappedBy = "week", fetch = FetchType.LAZY)
    private CashPage_txt cashPageTxt;



}
