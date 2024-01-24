package homeat.backend.domain.homeatreport.entity;

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

    @ManyToOne(fetch = FetchType.LAZY) // HomeatReport와의 관계에서 '다'
    @JoinColumn(name = "homeat_report_id") // FK 이름이 homeatReport_id
    private HomeatReport homeatReport;

    // WeekStatus(enum) : SUCCESS, FAIL, UNDO
    @Enumerated(EnumType.STRING)
    private WeekStatus week_status;

    @OneToOne(mappedBy = "week", fetch = FetchType.LAZY)
    private BadgePage_txt badgePageTxt;

}
