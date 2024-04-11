package homeat.backend.domain.address.repository.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.spatial.SpatialOps;
import com.querydsl.spatial.locationtech.jts.JTSGeometryExpressions;
import homeat.backend.domain.address.dto.AddressResponse;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;

import java.util.List;

import static homeat.backend.domain.address.entity.QAddress.address;

public class AddressRepositoryImpl implements AddressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AddressRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public AddressResponse.AddressDTO findFirstByPointDistance(double lat, double lng) {
        return queryFactory
                .select(Projections.constructor(AddressResponse.AddressDTO.class,
                        address.id,
                        address.code,
                        address.fullNm,
                        address.emdNm))
                .from(address)
                .orderBy(stDistance(lat, lng).asc())
                .fetchFirst();
    }

    @Override
    public Slice<AddressResponse.AddressDTO> findAllByOrderByDistanceAsc(double lat, double lng, Pageable pageable) {
        List<AddressResponse.AddressDTO> contents = queryFactory
                .select(Projections.constructor(AddressResponse.AddressDTO.class,
                        address.id,
                        address.code,
                        address.fullNm,
                        address.emdNm))
                .from(address)
                .orderBy(stDistance(lat, lng).asc())
                .limit(pageable.getPageSize()+1L)
                .offset(pageable.getOffset())
                .fetch();

        return new SliceImpl<>(contents, pageable, hasNext(contents, pageable.getPageSize()));
    }

    @Override
    public Slice<AddressResponse.AddressDTO> findByFullNmContainingOrderByDistanceAsc(double lat, double lng, String keyword, Pageable pageable) {
        List<AddressResponse.AddressDTO> contents = queryFactory
                .select(Projections.constructor(AddressResponse.AddressDTO.class,
                        address.id,
                        address.code,
                        address.fullNm,
                        address.emdNm))
                .from(address)
                .where(address.fullNm.contains(keyword))
                .orderBy(stDistance(lat, lng).asc())
                .limit(pageable.getPageSize()+1L)
                .offset(pageable.getOffset())
                .fetch();

        return new SliceImpl<>(contents, pageable, hasNext(contents, pageable.getPageSize()));
    }


    private Point createPoint(double lat, double lng) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel());
        return geometryFactory.createPoint(new Coordinate(lat, lng));
    }

    private NumberExpression<Double> stDistance(double lat, double lng) {
        Point currentPoint = createPoint(lat, lng);
        return Expressions.numberOperation(Double.class, SpatialOps.DISTANCE, address.point, JTSGeometryExpressions.asJTSGeometry(currentPoint));
    }

    private boolean hasNext(List<AddressResponse.AddressDTO> contents, int pageSize) {
        if(contents.size() > pageSize) {
            contents.remove(pageSize);
            return true;
        }
        return false;
    }
}
