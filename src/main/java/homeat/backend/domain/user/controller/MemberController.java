package homeat.backend.domain.user.controller;

import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberCommandService;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "회원 관련 api")
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @Operation(summary = "회원가입 api")
    @PostMapping("/join")
    public ApiPayload<MemberResponse.JoinResultDTO> create(@RequestBody @Valid MemberRequest.JoinDto request) {
        Member member = memberCommandService.joinMember(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, MemberConverter.toJoinResultDTO(member));
    }

    @Operation(summary = "로그인 api")
    @PostMapping("/login")
    public ApiPayload<MemberResponse.LoginResultDTO> login(@RequestBody @Valid MemberRequest.LoginDto request) {
        String token = memberCommandService.loginMember(request);
        LocalDateTime expiredAt = memberCommandService.getJwtExpiredAt(token);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberConverter.toLoginResultDTO(token, expiredAt));
    }

    @Operation(summary = "회원정보 api")
    @GetMapping("/mypage")
    public ApiPayload<MemberResponse.MyPageResultDTO> mypage(Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberConverter.toMyPageResultDTO(member));
    }
}
