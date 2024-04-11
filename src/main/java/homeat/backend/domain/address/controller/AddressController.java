package homeat.backend.domain.address.controller;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.service.AddressService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Address", description = "동네 관련 api")
@RequestMapping("/v1/address")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "가장 가까운 동네 1개 조회 api")
    @GetMapping("/closest")
    public ApiPayload<AddressResponse.AddressDTO> getAddressClosest(@RequestParam("latitude") double lat,
                                                                    @RequestParam("longitude") double lng) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, addressService.getClosestAddress(lat, lng));
    }

    @Operation(summary = "가까운 동네 페이징 조회 api")
    @GetMapping("")
    public ApiPayload<Slice<AddressResponse.AddressDTO>> getAddressList(@RequestParam("latitude") double lat,
                                                                        @RequestParam("longitude") double lng,
                                                                        @RequestParam("pageNum") int pageNum) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, addressService.getCloseAddressList(lat, lng, pageNum));
    }

    @Operation(summary = "가까운 동네 검색 페이징 api")
    @GetMapping("/search")
    public ApiPayload<Slice<AddressResponse.AddressDTO>> getAddressSearchList(@RequestParam("latitude") double lat,
                                                                            @RequestParam("longitude") double lng,
                                                                            @RequestParam("keyword") String keyword,
                                                                            @RequestParam("pageNum") int pageNum) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, addressService.getCloseAddressSearchList(lat, lng, keyword, pageNum));
    }
}
