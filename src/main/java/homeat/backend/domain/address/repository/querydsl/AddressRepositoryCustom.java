package homeat.backend.domain.address.repository.querydsl;

import homeat.backend.domain.address.dto.AddressResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface AddressRepositoryCustom {
    AddressResponse.GetQueryDTO findFirstByPointDistance(Double x, Double y);
    Slice<AddressResponse.GetQueryDTO> findAllByOrderByDistanceAsc(Double x, Double y, Pageable pageable);
}
