package homeat.backend.domain.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
