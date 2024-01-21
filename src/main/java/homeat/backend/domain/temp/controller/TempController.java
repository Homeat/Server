package homeat.backend.domain.temp.controller;

import homeat.backend.domain.temp.dto.TempResponse;
import homeat.backend.global.payload.ApiPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/temp")
@RequiredArgsConstructor
public class TempController {

    @GetMapping("/test")
    public ApiPayload<TempResponse.TempTestDTO> testAPI() {
        return ApiPayload.onSuccess("200", "테스트 API", TempConverter.toTempTestDTO());
    }
}
