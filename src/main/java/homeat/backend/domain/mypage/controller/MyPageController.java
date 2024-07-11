package homeat.backend.domain.mypage.controller;

import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.mypage.dto.MyPageResponse;
import homeat.backend.domain.mypage.service.MyPageService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다\n\nMEMBER_4043 : 존재하지 않는 주소입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("")
    public ApiPayload<?> postMyPage(@AuthenticationPrincipal CustomUserDetails authentication,
                                    @RequestBody @Valid MyPageRequest.postInfoDto request) {
        myPageService.insertInfo(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    @Operation(summary = "부가 회원정보 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("")
    public ApiPayload<MyPageResponse.getInfoDto> getMyPage(@AuthenticationPrincipal CustomUserDetails authentication) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, myPageService.selectInfo(authentication.getUserId()));
    }

    @Operation(summary = "부가 회원정보 상세 조회 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다\n\nMEMBER_4042 : 존재하지 않는 회원 정보입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("/detail")
    public ApiPayload<MyPageResponse.getDetailInfoDto> getDetailMyPage(@AuthenticationPrincipal CustomUserDetails authentication) {
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, myPageService.selectDetailInfo(authentication.getUserId()));
    }

    @Operation(summary = "닉네임 수정 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다\n\nMEMBER_4042 : 존재하지 않는 회원 정보입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping("/nickname")
    public ApiPayload<?> updateNicknameInfo(@AuthenticationPrincipal CustomUserDetails authentication,
                                            @RequestBody @Valid MyPageRequest.updateNicknameDto request) {
        myPageService.updateNickname(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "수입 수정 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다\n\nMEMBER_4042 : 존재하지 않는 회원 정보입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping("/income")
    public ApiPayload<?> updateIncomeInfo(@AuthenticationPrincipal CustomUserDetails authentication,
                                            @RequestBody @Valid MyPageRequest.updateIncomeDto request) {
        myPageService.updateIncome(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "닉네임 중복 확인 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "409", description = "MEMBER_4090 : 이미 존재하는 이메일입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/exist-nickname")
    public ApiPayload<?> existNickname(@RequestBody @Valid MyPageRequest.postExistNicknameDto request) {
        myPageService.existNickname(request.getNickname());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "비밀번호 변경 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "MEMBER_4010 : 비밀번호가 일치하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping("/change-password")
    public ApiPayload<?> updatePassword(@AuthenticationPrincipal CustomUserDetails authentication,
                                        @RequestBody @Valid MyPageRequest.patchPasswordDto request) {
        myPageService.updatePassword(authentication.getUserId(), request);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 수정 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> updateProfileImg(@AuthenticationPrincipal CustomUserDetails authentication,
                                          @RequestParam("profileImg") MultipartFile profileImg) {
        myPageService.updateProfileImg(authentication.getUserId(), profileImg);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "프로필 사진 삭제 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @DeleteMapping("/profile")
    public ApiPayload<?> deleteProfileImg(@AuthenticationPrincipal CustomUserDetails authentication) {
        myPageService.deleteProfileImg(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    @Operation(summary = "회원탈퇴(비활성) api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "MEMBER_4041 : 존재하지 않는 회원입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PatchMapping("/withdraw")
    public ApiPayload<?> withdraw(@AuthenticationPrincipal CustomUserDetails authentication) {
        myPageService.withdraw(authentication.getUserId());
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }
}