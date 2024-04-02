package homeat.backend.domain.address.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.address.entity.Address;

import javax.persistence.EntityManager;

import static homeat.backend.domain.address.entity.QAddress.address;

public class AddressRepositoryImpl implements AddressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AddressRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Address findAddressById(Long id) {
        return queryFactory
                .select(address)
                .from(address)
                .where(address.id.eq(id))
                .fetchOne();

    }
}
