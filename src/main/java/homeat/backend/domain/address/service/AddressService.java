package homeat.backend.domain.address.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponse.NeighborhoodResultDTO getAddress(Double x, Double y) {
        Object[] result = addressRepository.findOrderByPoint(x, y, 1, 0).get(0);
        return AddressResponse.NeighborhoodResultDTO.builder()
                .addressId((BigInteger) result[0])
                .fullNm((String) result[1])
                .emdNm((String) result[2])
                .build();
    }

    public List<AddressResponse.NeighborhoodResultDTO> getNegiborhood(Double x, Double y, int page) {
        List<Object[]> neighborhoods = addressRepository.findOrderByPoint(x, y, 20, page * 20);
        return neighborhoods.stream()
                .map(result -> AddressResponse.NeighborhoodResultDTO.builder()
                        .addressId((BigInteger) result[0])
                        .fullNm((String) result[1])
                        .emdNm((String) result[2])
                        .build())
                .collect(Collectors.toList());
    }

    public List<AddressResponse.NeighborhoodResultDTO> getNegiborhoodWithKeyword(Double x, Double y, String keyword, int page) {
        List<Object[]> neighborhoods = addressRepository.findByKeywordOrderByPoint(x, y, keyword, 20, page * 20);
        return neighborhoods.stream()
                .map(result -> AddressResponse.NeighborhoodResultDTO.builder()
                        .addressId((BigInteger) result[0])
                        .fullNm((String) result[1])
                        .emdNm((String) result[2])
                        .build())
                .collect(Collectors.toList());
    }

    public AddressResponse.NeighborhoodResultDTO getAddressInfoById(Long addressId) {
        Object[] result = addressRepository.findByIdCustom(addressId).get(0);
        return AddressResponse.NeighborhoodResultDTO.builder()
                .addressId((BigInteger) result[0])
                .fullNm((String) result[1])
                .emdNm((String) result[2])
                .build();
    }

    public Long getTotalCount() {
        return addressRepository.count();
    }

    public Long getTotalCountByKeyword(String keyword) {
        return addressRepository.countByKeyword(keyword).get(0);
    }

    public Object test() {
//        Address add = addressRepository.findByPointDistance(126.9221, 37.5617).get(0);
//        Address add = addressRepository.findFirstByPointDistance(126.9221, 37.5617);
        Pageable pageable = PageRequest.of(0, 10);
        return addressRepository.findSliceByPointDistance(126.9221, 37.5617, pageable);
    }
}
