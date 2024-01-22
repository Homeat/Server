package homeat.backend.domain.homeatcash.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class CashPage_txt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_page_txt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_img_id")
    private Badge_img badge_img;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id")
    private Week week;

    private Long week_cash;
}
