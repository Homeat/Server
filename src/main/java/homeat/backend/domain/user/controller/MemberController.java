package homeat.backend.domain.user.controller;

import homeat.backend.domain.user.dto.AddressResponse;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Address;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.service.AddressService;
import homeat.backend.domain.user.service.MemberCommandService;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "회원 관련 api")
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final AddressService addressService;

    @Operation(summary = "회원가입 api")
    @PostMapping("/join")
    public ApiPayload<MemberResponse.JoinResultDTO> create(@RequestBody @Valid MemberRequest.JoinDto request) {
        Member member = memberCommandService.joinMember(request);
        String token = memberCommandService.loginMember(member.getId());
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, MemberConverter.toJoinResultDTO(member, token));
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
        Long memberId = Long.parseLong(authentication.getName());
        Member member = memberQueryService.mypageMember(memberId);
        MemberInfo memberInfo = memberQueryService.mypageMemberInfo(memberId);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberConverter.toMyPageResultDTO(member, memberInfo));
    }

    @Operation(summary = "회원가입시, 부가 회원정보 추가 api")
    @PostMapping("/mypage")
    public ApiPayload<MemberResponse.CreateInfoResultDTO> createMypage(@RequestBody @Valid MemberRequest.CreateInfoDto request, Authentication authentication) {
        MemberInfo memberInfo = memberCommandService.saveMemberInfo(request, Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, MemberConverter.toCreateInfoResultDTO(memberInfo));
    }

    @Operation(summary = "회원가입시, 이메일 인증 요청 api")
    @PostMapping("/email-verification")
    public ApiPayload<MemberResponse.EmailVerifyDto> emailVerificationReq(@RequestBody @Valid MemberRequest.EmailVerifyDto request) {
        String authCode = memberCommandService.sendCodeToEmail(request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberConverter.toEmailVerifyDTO(authCode));
    }

    @Operation(summary = "비밀번호 변경 api")
    @PatchMapping("/mypage/password")
    public ApiPayload<?> updatePassword(@RequestBody @Valid MemberRequest.UpdatePasswordDto request, Authentication authentication) {
        memberCommandService.updatePassword(request, Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원정보 수정 api")
    @PatchMapping("/mypage")
    public ApiPayload<?> updateInfo(@RequestBody @Valid MemberRequest.UpdateInfoDto request, Authentication authentication) {
        memberCommandService.updateInfo(request, Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 수정 api")
    @PatchMapping(value = "/mypage/profileImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> updateProfileImg(@RequestParam("profileImg") MultipartFile multipartProfileImg, Authentication authentication) {
        memberCommandService.updateProfileImg(multipartProfileImg, Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 삭제 api")
    @PatchMapping("/mypage/profileImg/delete")
    public ApiPayload<?> deleteProfileImg(Authentication authentication) {
        memberCommandService.deleteProfileImg(Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원탈퇴(비활성) api")
    @PatchMapping("/mypage/withdraw")
    public ApiPayload<?> withdrawal(Authentication authentication) {
        memberCommandService.withdraw(Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원 재활성 api")
    @PatchMapping("/mypage/reactivate")
    public ApiPayload<?> reactivate(Authentication authentication) {
        memberCommandService.reactivate(Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "주변 동네 조회 api")
    @GetMapping("/address/neighborhood")
    public ApiPayload<MemberResponse.GetNeighborhoodResultDTO> neighborhood(@RequestParam("latitude") Double x,
                                      @RequestParam("logitude") Double y,
                                      @RequestParam("page") int page) {
        List<AddressResponse.NeighborhoodResultDTO> neighborhoods = addressService.getNegiborhood(x, y, page);
        Long totalColumnCount = addressService.getTotalCount();



        return ApiPayload.onSuccess(CommonSuccessStatus.OK, MemberResponse.GetNeighborhoodResultDTO.builder()
                        .totalColumnCount(totalColumnCount)
                        .totlaPageNum((totalColumnCount / 20) + 1)
                        .neighborhoods(neighborhoods)
                        .build());
    }
}
