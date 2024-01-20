package homeat.backend.domain.homeatcash;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Badge_img extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_img_id")
    private Long id;

    @OneToMany(mappedBy = "badgeImg")
    private List<BadgePage_txt> badgePageTxtList = new ArrayList<>();

    @OneToMany(mappedBy = "badgeImg")
    private List<CashPage_txt> cashPageTxtList = new ArrayList<>();

    private String image_url;
}
