package homeat.backend.domain.mypage.controller;

import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.mypage.dto.MyPageResponse;
import homeat.backend.domain.mypage.service.MyPageService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "MyPage", description = "회원 정보 관련 api")
@RequestMapping("/v1/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @Operation(summary = "회원가입시, 부가 회원정보 추가 api")
    @PostMapping("")
    public ApiPayload<?> postMyPage(@AuthenticationPrincipal CustomUserDetails authentication,
                                    @RequestBody @Valid MyPageRequest.postInfoDto request) {
        myPageService.insertInfo(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    @Operation(summary = "부가 회원정보 조회 api")
    @GetMapping("")
    public ApiPayload<MyPageResponse.getInfoDto> getMyPage(@AuthenticationPrincipal CustomUserDetails authentication) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, myPageService.selectInfo(authentication.getUserId()));
    }

    @Operation(summary = "부가 회원정보 상세 조회 api")
    @GetMapping("/detail")
    public ApiPayload<MyPageResponse.getDetailInfoDto> getDetailMyPage(@AuthenticationPrincipal CustomUserDetails authentication) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, myPageService.selectDetailInfo(authentication.getUserId()));
    }

    @Operation(summary = "회원정보 수정 api")
    @PatchMapping("")
    public ApiPayload<?> updateInfo(@AuthenticationPrincipal CustomUserDetails authentication,
                                    @RequestBody @Valid MyPageRequest.patchInfoDto request) {
        myPageService.updateInfo(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "닉네임 중복 확인 api")
    @PostMapping("/exist-nickname")
    public ApiPayload<?> existNickname(@RequestBody @Valid MyPageRequest.postExistNicknameDto request) {
        myPageService.existNickname(request.getNickname());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "비밀번호 변경 api")
    @PatchMapping("/change-password")
    public ApiPayload<?> updatePassword(@AuthenticationPrincipal CustomUserDetails authentication,
                                        @RequestBody @Valid MyPageRequest.patchPasswordDto request) {
        myPageService.updatePassword(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 수정 api")
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> updateProfileImg(@AuthenticationPrincipal CustomUserDetails authentication,
                                          @RequestParam("profileImg") MultipartFile profileImg) {
        myPageService.updateProfileImg(authentication.getUserId(), profileImg);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 삭제 api")
    @DeleteMapping("/profile")
    public ApiPayload<?> deleteProfileImg(@AuthenticationPrincipal CustomUserDetails authentication) {
        myPageService.deleteProfileImg(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원탈퇴(비활성) api")
    @PatchMapping("/withdraw")
    public ApiPayload<?> withdraw(@AuthenticationPrincipal CustomUserDetails authentication) {
        myPageService.withdraw(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원 재활성 api")
    @PatchMapping("/reactivate")
    public ApiPayload<?> reactivate(@AuthenticationPrincipal CustomUserDetails authentication) {
        myPageService.reactivate(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }
}