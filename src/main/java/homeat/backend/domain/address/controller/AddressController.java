package homeat.backend.domain.address.controller;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.service.AddressService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import homeat.backend.global.payload.SlicePayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "가까운 동네 무한스크롤 조회 api")
    @GetMapping("")
    public SlicePayload<AddressResponse.AddressDTO> getAddressList(@RequestParam("latitude") double lat,
                                                                   @RequestParam("longitude") double lng,
                                                                   @RequestParam("pageNum") int pageNum) {
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, addressService.getCloseAddressList(lat, lng, pageNum));
    }

    @Operation(summary = "가까운 동네 검색 무한스크롤 api")
    @GetMapping("/search")
    public SlicePayload<AddressResponse.AddressDTO> getAddressSearchList(@RequestParam("latitude") double lat,
                                                                         @RequestParam("longitude") double lng,
                                                                         @RequestParam("keyword") String keyword,
                                                                         @RequestParam("pageNum") int pageNum) {
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, addressService.getCloseAddressSearchList(lat, lng, keyword, pageNum));
    }
}
