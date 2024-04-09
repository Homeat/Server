package homeat.backend.domain.address.repository.querydsl;

import homeat.backend.domain.address.entity.Address;


public interface AddressRepositoryCustom {
    Address findFirstByPointDistance(Double x, Double y);
}
