package homeat.backend.domain.homeatcash;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class BadgePage_txt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_page_txt_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "is_success_id")
    private IsSuccess isSuccess;

    @ManyToOne
    @JoinColumn(name = "badge_img_id")
    private Badge_img badgeImg;

    private Long exceed_price;
}
