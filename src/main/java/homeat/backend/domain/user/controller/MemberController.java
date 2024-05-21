package homeat.backend.domain.user.controller;


import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.service.MemberMapper;
import homeat.backend.domain.user.service.MemberService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "AUTH_4000 : 잘못된 파라미터 형식입니다", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "AUTH_4010 : 로그인 정보가 잘못되었습니다\n\nAUTH_4011 : 토큰이 존재하지 않습니다\n\nAUTH_4012 : 토큰이 만료되었습니다\n\nAUTH_4013 : 토큰이 올바르지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요\n\nAUTH_5000 : 서버 출력에 오류가 있습니다. 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/login")
    public ApiPayload<?> login(@RequestBody MemberRequest.loginDto request) {
        // Filter에서 작동하지만, Swagger 위해서 틀만 작성
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "로그아웃 api", description = "Cookie에 refresh 토큰 필요")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "AUTH_4000 : 잘못된 파라미터 형식입니다", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "AUTH_4010 : 로그인 정보가 잘못되었습니다\n\nAUTH_4011 : 토큰이 존재하지 않습니다\n\nAUTH_4012 : 토큰이 만료되었습니다\n\nAUTH_4013 : 토큰이 올바르지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요\n\nAUTH_5000 : 서버 출력에 오류가 있습니다. 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/logout")
    public ApiPayload<?> logout() {
        // Filter에서 작동하지만, Swagger 위해서 틀만 작성
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "토큰 재발급 api", description = "Cookie에 기존 refresh 토큰 필요, 헤더의 Authorization에 access 토큰, 쿠키에 refresh 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "AUTH_4000 : 잘못된 파라미터 형식입니다", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "AUTH_4010 : 로그인 정보가 잘못되었습니다\n\nAUTH_4011 : 토큰이 존재하지 않습니다\n\nAUTH_4012 : 토큰이 만료되었습니다\n\nAUTH_4013 : 토큰이 올바르지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요\n\nAUTH_5000 : 서버 출력에 오류가 있습니다. 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/reissue")
    public ApiPayload<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        memberService.reissueToken(request, response);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "비밀번호 찾기(인증 후, 재설정) api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4040 : 존재하지 않는 이메일입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping("/find-password")
    public ApiPayload<?> findPassword(@RequestBody @Valid MemberRequest.findPasswordDto request) {
        memberService.findPassword(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원 가입시, 이메일 인증 요청 api(이메일이 중복되지 않아야 함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nMEMBER_4000 : 메일을 전송할 수 없습니다\n\nMEMBER_4001 : 잘못된 코드 알고리즘입니다", content = {@Content()}),
            @ApiResponse(responseCode = "409", description = "MEMBER_4090 : 이미 존재하는 이메일입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/email-cerification")
    public ApiPayload<MemberResponse.emailCheckDto> emailCerification(@RequestBody @Valid MemberRequest.emailCheckDto request) {
        String authCode = memberService.certifyEmail(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberMapper.toEmailCheck(authCode));
    }

    @Operation(summary = "비밀번호 찾기시, 이메일 검증 요청 api(가입된 이메일이 존재해야 함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nMEMBER_4000 : 메일을 전송할 수 없습니다\n\nMEMBER_4001 : 잘못된 코드 알고리즘입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4040 : 존재하지 않는 이메일입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/email-verification")
    public ApiPayload<MemberResponse.emailCheckDto> emailVerification(@RequestBody @Valid MemberRequest.emailCheckDto request) {
        String authCode = memberService.verifyEmail(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberMapper.toEmailCheck(authCode));
    }
}