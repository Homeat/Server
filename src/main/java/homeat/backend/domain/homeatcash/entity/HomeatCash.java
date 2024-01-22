package homeat.backend.domain.homeatcash.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import homeat.backend.domain.user.entity.Member;

@Entity
@Getter
public class HomeatCash extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "homeat_cash_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long total_success;

    private Long cont_success;

    private Long total_cash;

    @OneToMany(mappedBy = "homeatCash")
    private List<Week> weeks = new ArrayList<>();
}
