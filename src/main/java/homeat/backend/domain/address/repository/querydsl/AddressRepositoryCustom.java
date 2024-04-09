package homeat.backend.domain.address.repository.querydsl;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;


public interface AddressRepositoryCustom {
    Address findFirstByPointDistance(Double x, Double y);
    AddressResponse.GetQueryDTO findFirstByPointDistance(Double x, Double y);
}
