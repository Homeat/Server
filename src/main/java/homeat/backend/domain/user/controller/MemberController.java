package homeat.backend.domain.user.controller;

import homeat.backend.domain.address.controller.AddressConvertor;
import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.service.MemberCommandService;
import homeat.backend.domain.user.service.MemberMapper;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.domain.user.service.MemberService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import homeat.backend.global.security.LoginService;
import homeat.backend.global.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "회원 관련 api")
@RequestMapping("/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

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
    public ApiPayload<?> findPassword(@RequestBody @Valid MemberRequest.FindPasswordDto request) {
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

    @Operation(summary = "회원정보 api")
    @GetMapping("/mypage")
    public ApiPayload<MemberResponse.MyPageResultDTO> mypage(@AuthenticationPrincipal CustomUserDetails authentication) {
        Long memberId = authentication.getUserId();
        Member member = memberQueryService.mypageMember(memberId);
        MemberInfo memberInfo = memberQueryService.mypageMemberInfo(memberId);
        AddressResponse.AddressDTO addressInfo = AddressConvertor.toAddressInfo(memberInfo.getAddress());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberMapper.toMyPageResultDTO(member, memberInfo, addressInfo));
    }

    @Operation(summary = "회원가입시, 부가 회원정보 추가 api")
    @PostMapping("/mypage")
    public ApiPayload<MemberResponse.CreateInfoResultDTO> createMypage(@RequestBody @Valid MemberRequest.CreateInfoDto request, @AuthenticationPrincipal CustomUserDetails authentication) {
        MemberInfo memberInfo = memberCommandService.saveMemberInfo(request, authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, MemberMapper.toCreateInfoResultDTO(memberInfo));
    }

    @Operation(summary = "비밀번호 변경 api")
    @PatchMapping("/mypage/password")
    public ApiPayload<?> updatePassword(@RequestBody @Valid MemberRequest.UpdatePasswordDto request, @AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.updatePassword(request, authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원정보 수정 api")
    @PatchMapping("/mypage")
    public ApiPayload<?> updateInfo(@RequestBody @Valid MemberRequest.UpdateInfoDto request, @AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.updateInfo(request, authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 수정 api")
    @PatchMapping(value = "/mypage/profileImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> updateProfileImg(@RequestParam("profileImg") MultipartFile multipartProfileImg, @AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.updateProfileImg(multipartProfileImg, authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 삭제 api")
    @PatchMapping("/mypage/profileImg/delete")
    public ApiPayload<?> deleteProfileImg(@AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.deleteProfileImg(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원탈퇴(비활성) api")
    @PatchMapping("/mypage/withdraw")
    public ApiPayload<?> withdrawal(@AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.withdraw(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원 재활성 api")
    @PatchMapping("/mypage/reactivate")
    public ApiPayload<?> reactivate(@AuthenticationPrincipal CustomUserDetails authentication) {
        memberCommandService.reactivate(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }
}
