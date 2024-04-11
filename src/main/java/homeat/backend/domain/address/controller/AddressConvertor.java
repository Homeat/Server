package homeat.backend.domain.address.controller;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;

public class AddressConvertor {
    public static AddressResponse.AddressDTO toAddressInfo(Address address) {
        return AddressResponse.AddressDTO.builder()
                .addressId(address.getId())
                .code(address.getCode())
                .fullNm(address.getFullNm())
                .emdNm(address.getEmdNm())
                .build();
    }
}
