package homeat.backend.domain.homeatcash;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class IsSuccess extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "is_success_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //HomeatCash와의 관계에서 '다'
    @JoinColumn(name = "homeat_cash_id") //FK 이름이 homeatCash_id
    private HomeatCash homeatCash;

    // WeekStatus(enum) : SUCCESS, FAIL, UNDO

    @Enumerated(EnumType.STRING)
    private WeekStatus week1;

    @Enumerated(EnumType.STRING)
    private WeekStatus week2;

    @Enumerated(EnumType.STRING)
    private WeekStatus week3;

    @Enumerated(EnumType.STRING)
    private WeekStatus week4;

    @Enumerated(EnumType.STRING)
    private WeekStatus week5;

    @Enumerated(EnumType.STRING)
    private WeekStatus week6;

    @Enumerated(EnumType.STRING)
    private WeekStatus week7;

    @Enumerated(EnumType.STRING)
    private WeekStatus week8;

    @Enumerated(EnumType.STRING)
    private WeekStatus week9;

    @OneToMany(mappedBy = "isSuccess") //BadgePage_txt와의 관계에서 '일'
    private List<BadgePage_txt> badgePageTxtList = new ArrayList<>();

    @OneToMany(mappedBy = "isSuccess") //CashPage_txt와의 관계에서 '일'
    private List<CashPage_txt> cashPageTxtList = new ArrayList<>();
}
