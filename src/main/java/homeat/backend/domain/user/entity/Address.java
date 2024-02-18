package homeat.backend.domain.user.entity;

import homeat.backend.global.common.domain.BaseEntity;
import lombok.*;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

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

    private Point coord;
}
