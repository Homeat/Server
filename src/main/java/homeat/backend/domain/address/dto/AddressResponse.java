package homeat.backend.domain.address.dto;

import lombok.*;

import java.math.BigInteger;
import java.util.List;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNeighborhoodResultDTO {
        Long totalColumnCount;
        Long totlaPageNum;
        List<NeighborhoodResultDTO> neighborhoods;
    }

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
