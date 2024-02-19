package homeat.backend.domain.address.controller;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.service.AddressService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Address", description = "동네 관련 api")
@RequestMapping("/v1/address")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "동네 조회 api")
    @GetMapping()
    public ApiPayload<AddressResponse.NeighborhoodResultDTO> address(@RequestParam("latitude") Double x,
                                                                     @RequestParam("logitude") Double y) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, addressService.getAddress(x, y));
    }

    @Operation(summary = "주변 동네 조회 api")
    @GetMapping("/neighborhood")
    public ApiPayload<AddressResponse.GetNeighborhoodResultDTO> neighborhood(@RequestParam("latitude") Double x,
                                                                            @RequestParam("logitude") Double y,
                                                                            @RequestParam("page") int page) {

        List<AddressResponse.NeighborhoodResultDTO> neighborhoods = addressService.getNegiborhood(x, y, page);
        Long totalColumnCount = addressService.getTotalCount();

        return ApiPayload.onSuccess(CommonSuccessStatus.OK, AddressResponse.GetNeighborhoodResultDTO.builder()
                .totalColumnCount(totalColumnCount)
                .totlaPageNum((totalColumnCount / 20) + 1)
                .neighborhoods(neighborhoods)
                .build());
    }

    @Operation(summary = "키워드 활용해서 주변 동네 조회 api")
    @GetMapping("/neighboorhood/keyword")
    public ApiPayload<AddressResponse.GetNeighborhoodResultDTO> address(@RequestParam("latitude") Double x,
                                                                        @RequestParam("logitude") Double y,
                                                                        @RequestParam("keyword") String keyword,
                                                                        @RequestParam("page") int page) {
        List<AddressResponse.NeighborhoodResultDTO> neighborhoods = addressService.getNegiborhoodWithKeyword(x, y, keyword, page);
        Long totalColumnCount = addressService.getTotalCountByKeyword(keyword);

        return ApiPayload.onSuccess(CommonSuccessStatus.OK, AddressResponse.GetNeighborhoodResultDTO.builder()
                .totalColumnCount(totalColumnCount)
                .totlaPageNum((totalColumnCount / 20) + 1)
                .neighborhoods(neighborhoods)
                .build());
    }
}
