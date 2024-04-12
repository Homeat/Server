package homeat.backend.domain.address.dto;

import lombok.*;

public class AddressResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        Long addressId;
        Long code;
        String fullNm;
        String emdNm;
    }
}
