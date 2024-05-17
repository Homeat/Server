package homeat.backend.domain.user.controller;


import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.service.MemberMapper;
import homeat.backend.domain.user.service.MemberService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "회원 관련 api")
@RequestMapping("/v1/members")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "이메일 회원가입 api")
    @PostMapping("/join/email")
    public ApiPayload<?> joinByEmail(HttpServletResponse response,
                                     @RequestBody @Valid MemberRequest.joinEmailDto requestDto) {
        memberService.insertMemberByEmail(response, requestDto);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

//    @Operation(summary = "카카오 회원가입 api")
//    @PostMapping("/join/kakao")
//    public ApiPayload<?> joinByKakao(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     @RequestBody @Valid MemberRequest.joinKakaoDto requestDto) {
//        memberService.insertMemberByKakao(request, response, requestDto);
//        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
//    }

    @Operation(summary = "로그인 api", description = "헤더의 Authorization에 access 토큰, 쿠키에 refresh 토큰 반환")
    @PostMapping("/login")
    public ApiPayload<?> login(@RequestBody MemberRequest.loginDto request) {
        // Filter에서 작동하지만, Swagger 위해서 틀만 작성
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "로그아웃 api", description = "Cookie에 refresh 토큰 필요")
    @PostMapping("/logout")
    public ApiPayload<?> logout() {
        // Filter에서 작동하지만, Swagger 위해서 틀만 작성
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "토큰 재발급 api", description = "Cookie에 기존 refresh 토큰 필요, 헤더의 Authorization에 access 토큰, 쿠키에 refresh 토큰 반환")
    @PostMapping("/reissue")
    public ApiPayload<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        memberService.reissueToken(request, response);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "비밀번호 찾기(인증 후, 재설정) api")
    @PatchMapping("/find-password")
    public ApiPayload<?> findPassword(@RequestBody @Valid MemberRequest.findPasswordDto request) {
        memberService.findPassword(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원 가입시, 이메일 인증 요청 api(이메일이 중복되지 않아야 함)")
    @PostMapping("/email-cerification")
    public ApiPayload<MemberResponse.emailCheckDto> emailCerification(@RequestBody @Valid MemberRequest.emailCheckDto request) {
        String authCode = memberService.certifyEmail(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberMapper.toEmailCheck(authCode));
    }

    @Operation(summary = "비밀번호 찾기시, 이메일 검증 요청 api(가입된 이메일이 존재해야 함)")
    @PostMapping("/email-verification")
    public ApiPayload<MemberResponse.emailCheckDto> emailVerification(@RequestBody @Valid MemberRequest.emailCheckDto request) {
        String authCode = memberService.verifyEmail(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberMapper.toEmailCheck(authCode));
    }
}
