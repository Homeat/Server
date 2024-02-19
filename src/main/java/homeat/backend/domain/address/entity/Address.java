package homeat.backend.domain.address.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    private Long code;

    private String full_nm;

    private String emd_nm;

    @Column(precision = 9, scale = 6)
    private BigDecimal x_coord;

    @Column(precision = 9, scale = 6)
    private BigDecimal y_coord;

    private Point point;
}
