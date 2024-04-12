package homeat.backend.domain.address.repository.querydsl;

import homeat.backend.domain.address.dto.AddressResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface AddressRepositoryCustom {
    AddressResponse.AddressDTO findFirstByPointDistance(double lat, double lng);
    Slice<AddressResponse.AddressDTO> findAllByOrderByDistanceAsc(double lat, double lng, Pageable pageable);
    Slice<AddressResponse.AddressDTO> findByFullNmContainingOrderByDistanceAsc(double lat, double lng, String keyword, Pageable pageable);
}
