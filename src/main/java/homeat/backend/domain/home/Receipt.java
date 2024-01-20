package homeat.backend.domain.home;

import homeat.backend.domain.common.BaseEntity;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "management_id")
//    private Managerment managerment;

    @Enumerated(EnumType.STRING)
    private CostType costType;

    private Long expense;

    @Column(columnDefinition = "TEXT" /*, nullable = false */)
    private String memo;

    private String url;
}
