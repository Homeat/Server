package homeat.backend.domain.user.service;

import homeat.backend.domain.user.dto.AddressResponse;
import homeat.backend.domain.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
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

    public Long getTotalCount() {
        return addressRepository.count();
    }
}
