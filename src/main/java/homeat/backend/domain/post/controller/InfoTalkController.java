package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.InfoRequestDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkSaveDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.service.InfoTalkService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/infoTalk")
@RequiredArgsConstructor
public class InfoTalkController {

    private final InfoTalkService infoTalkService;
    private final MemberQueryService memberQueryService;

    /**
     * 정보토크 저장
     */
    @Operation(summary = "정보토크 내용 저장 api")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiPayload<InfoResponseDTO.InfoTalkSaveDTO> saveInfoTalk(@RequestBody InfoRequestDTO.InfoTalkDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {

        Member member = memberQueryService.mypageMember(authentication.getUserId());
        InfoTalkSaveDTO result = infoTalkService.saveInfoTalk(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }

    /**
     * 정보토크 사진 업로드
     */
    @Operation(summary = "정보토크 사진 저장 api")
    @PostMapping(value = "/upload/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<String> uploadImages(@PathVariable("id") Long id,@RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }
        String result = infoTalkService.uploadImages(id, multipartFiles);

        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }


    /**
     * 정보토크 삭제
     */
    @Operation(summary = "정보토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ApiPayload<String> deleteInfoTalk(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = infoTalkService.deleteInfoTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "정보토크 게시글 수정 api, 개발X")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateInfoTalk(@RequestBody @Valid InfoRequestDTO.InfoTalkDTO dto, @PathVariable("id") Long id) {
        return infoTalkService.updateInfoTalk(dto, id);
    }

    /**
     * 정보토크 조회
     */
    @Operation(summary = "정보토크 게시글 1개 조회 api")
    @GetMapping("/{id}")
    public ApiPayload<InfoResponseDTO.InfoTalkViewDTO> getInfoTalk(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        InfoTalkViewDTO result = infoTalkService.getInfoTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 최신순 조회
     */
    @Operation(summary = "정보 최신순 조회 및 검색, lastInfoTalkId 보다 작은 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/latest")
    public ApiPayload<Slice<InfoTalkTotalView>> getInfoTalkLatest(InfoTalkSearchCondition condition, @RequestParam Long lastInfoTalkId) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkLatest(condition, lastInfoTalkId);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "정보토크 오래된 순 조회 및 검색, lastInfoTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/oldest")
    public ApiPayload<Slice<InfoTalkTotalView>> getInfoTalkOldest(InfoTalkSearchCondition condition,
                                                                  @RequestParam Long oldestInfoTalkId) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkOldest(condition, oldestInfoTalkId);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "정보토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/love")
    public ApiPayload<Slice<InfoTalkTotalView>> getInfoTalkByLove(InfoTalkSearchCondition condition, @RequestParam Long id, @RequestParam int love) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkByLove(condition, id, love);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "정보토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/view")
    public ApiPayload<Slice<InfoTalkTotalView>> getInfoTalkByView(InfoTalkSearchCondition condition,@RequestParam Long id,@RequestParam int view) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkByView(condition, id, view);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "정보토크 댓글 작성, id는 정보토크 게시물 id 입니다.")
    @PostMapping("/comment")
    public ApiPayload<String> saveComment(@RequestBody @Valid InfoRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {

        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = infoTalkService.saveComment(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        return infoTalkService.deleteComment(commentId, member);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "정보토크 대댓글 작성, id는 댓글 아이디입니다.")
    @PostMapping("/reply/{id}")
    public ResponseEntity<?> saveReply(@RequestBody @Valid InfoRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        return infoTalkService.saveReply(dto, member);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @DeleteMapping("/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        return infoTalkService.deleteReply(id, member);
    }

    /**
     * 공감하기
     */
    @Operation(summary = "정보토크 게시물 공감하기 api입니다. id는 정보토크 게시물 id 입니다")
    @PostMapping("/love/{id}")
    public ResponseEntity<?> saveLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        return infoTalkService.saveLove(id, member);
    }

    /**
     * 공감 취소하기
     */
    @Operation(summary = "정보토크 게시물 공감 취소하기, id는 정보토크 게시물 id 입니다")
    @DeleteMapping("/love/{id}")
    public ResponseEntity<?> deleteLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        return infoTalkService.deleteLove(id, member);
    }


}
