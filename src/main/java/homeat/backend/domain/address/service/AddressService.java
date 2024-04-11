package homeat.backend.domain.address.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;

    public Address getAddress(Long id) {
        return addressRepository.findById(id).orElseThrow();
    }

    public AddressResponse.AddressDTO getClosestAddress(double lat, double lng) {
        return addressRepository.findFirstByPointDistance(lat, lng);
    }

    public Slice<AddressResponse.AddressDTO> getCloseAddressList(double lat, double lng, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 20);
        return addressRepository.findAllByOrderByDistanceAsc(lat, lng, pageable);
    }

    public Slice<AddressResponse.AddressDTO> getCloseAddressSearchList(double lat, double lng, String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 20);
        return addressRepository.findByFullNmContainingOrderByDistanceAsc(lat, lng, keyword, pageable);
    }
}
