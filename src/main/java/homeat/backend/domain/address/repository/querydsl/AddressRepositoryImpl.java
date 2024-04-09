package homeat.backend.domain.address.repository.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.spatial.SpatialOps;
import com.querydsl.spatial.locationtech.jts.JTSGeometryExpressions;
import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.dto.QAddressResponse_GetQueryDTO;
import homeat.backend.domain.address.entity.Address;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.EntityManager;

import static homeat.backend.domain.address.entity.QAddress.address;

public class AddressRepositoryImpl implements AddressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AddressRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public AddressResponse.GetQueryDTO findFirstByPointDistance(Double x, Double y) {
        return queryFactory
                .select(new QAddressResponse_GetQueryDTO(address.id, address.code, address.fullNm, address.emdNm))
                .from(address)
                .orderBy(
                        getDistance(x,y).asc()
                )
                .fetchFirst();
    }


    public Point createPoint(double lat, double lng) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }

    public NumberExpression<Double> getDistance(double lat, double lng) {
        Point currentPoint = createPoint(lat, lng);
        return Expressions.numberOperation(Double.class, SpatialOps.DISTANCE, address.point, JTSGeometryExpressions.asJTSGeometry(currentPoint));
    }
}
