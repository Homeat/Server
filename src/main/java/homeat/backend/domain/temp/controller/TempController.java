package homeat.backend.domain.temp.controller;

import homeat.backend.domain.temp.dto.TempResponse;
import homeat.backend.domain.temp.service.TempQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/temp")
@RequiredArgsConstructor
public class TempController {

    private final TempQueryService tempQueryService;

    @GetMapping("/test")
    public ApiPayload<TempResponse.TempTestDTO> testAPI() {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, TempConverter.toTempTestDTO());
    }

    @GetMapping("/exception")
    public ApiPayload<TempResponse.TempExceptionDTO> exceptionAPI(@RequestParam Integer flag) {
        tempQueryService.CheckFlag(flag);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, TempConverter.toTempExceptionDTO(flag));
    }
}
