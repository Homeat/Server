package homeat.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

public class AddressResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NeighborhoodResultDTO {
        BigInteger addressId;
        String fullNm;
        String emdNm;
    }
}
