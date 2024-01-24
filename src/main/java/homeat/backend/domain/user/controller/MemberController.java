package homeat.backend.domain.user.controller;

import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberCommandService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/users")
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/join")
    public ApiPayload<MemberResponse.JoinResultDTO> create(@RequestBody @Valid MemberRequest.JoinDto request) {
        Member member = memberCommandService.joinMember(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, MemberConverter.toJoinResultDTO(member));
    }
}
