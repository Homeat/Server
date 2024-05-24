package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.InfoRequestDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.service.InfoTalkService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import homeat.backend.global.payload.SlicePayload;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> saveInfoTalk(@RequestParam(value = "title",required = false) String title,
                                                                    @RequestParam(value = "content",required = false) String content,
                                                                    @RequestParam(value = "tags",required = false) List<String> tags,
                                                                    @RequestParam(value = "imgUrl",required = false) List<MultipartFile> multipartFiles,
                                                                    @AuthenticationPrincipal CustomUserDetails authentication) {

        if (title == null) {
            throw new GeneralException(PostErrorStatus.POST_TITLE_PAYMENT_REQUIRED);
        }
        if (content == null) {
            throw new GeneralException(PostErrorStatus.POST_CONTENT_PAYMENT_REQUIRED);
        }
        if (multipartFiles == null) {
            throw new GeneralException(PostErrorStatus.POST_IMAGE_PAYMENT_REQUIRED);
        }



        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.saveInfoTalk(title,content,tags,multipartFiles, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }


    /**
     * 정보토크 삭제
     */
    @Operation(summary = "정보토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ApiPayload<?> deleteInfoTalk(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.deleteInfoTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 게시글 수정
     */
//    @Operation(summary = "정보토크 게시글 수정 api, 개발X")
//    @PatchMapping("/update/{id}")
//    public ResponseEntity<?> updateInfoTalk(@RequestBody @Valid InfoRequestDTO.InfoTalkDTO dto, @PathVariable("id") Long id) {
//        return infoTalkService.updateInfoTalk(dto, id);
//    }

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
    public SlicePayload<InfoTalkTotalView> getInfoTalkLatest(InfoTalkSearchCondition condition, @RequestParam Long lastInfoTalkId) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkLatest(condition, lastInfoTalkId);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "정보토크 오래된 순 조회 및 검색, lastInfoTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/oldest")
    public SlicePayload<InfoTalkTotalView> getInfoTalkOldest(InfoTalkSearchCondition condition,
                                                                  @RequestParam Long oldestInfoTalkId) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkOldest(condition, oldestInfoTalkId);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "정보토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/love")
    public SlicePayload<InfoTalkTotalView> getInfoTalkByLove(InfoTalkSearchCondition condition, @RequestParam Long id, @RequestParam int love) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkByLove(condition, id, love);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "정보토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/view")
    public SlicePayload<InfoTalkTotalView> getInfoTalkByView(InfoTalkSearchCondition condition,@RequestParam Long id,@RequestParam int view) {
        Slice<InfoTalkTotalView> result = infoTalkService.getInfoTalkByView(condition, id, view);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "정보토크 댓글 작성, id는 정보토크 게시물 id 입니다.")
    @PostMapping("/comment")
    public ApiPayload<?> saveComment(@RequestBody @Valid InfoRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {

        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.saveComment(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ApiPayload<?> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.deleteComment(commentId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "정보토크 대댓글 작성, id는 댓글 아이디입니다.")
    @PostMapping("/reply")
    public ApiPayload<?> saveReply(@RequestBody @Valid InfoRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.saveReply(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @DeleteMapping("/reply/{replyId}")
    public ApiPayload<?> deleteReply(@PathVariable("replyId") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.deleteReply(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 공감하기
     */
    @Operation(summary = "정보토크 게시물 공감하기 api입니다. id는 정보토크 게시물 id 입니다")
    @PostMapping("/love/{id}")
    public ApiPayload<?> saveLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.saveLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 공감 취소하기
     */
    @Operation(summary = "정보토크 게시물 공감 취소하기, id는 정보토크 게시물 id 입니다")
    @DeleteMapping("/love/{id}")
    public ApiPayload<?> deleteLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.deleteLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 정보토크 신고하기
     */
    @Operation(summary = "정보토크 게시물 신고하기, postId는 정보토크 게시물 id입니다.")
    @PostMapping("/report/{postId})")
    public ApiPayload<?> reportInfoTalk(@PathVariable("postId") Long postId,
                                        @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        infoTalkService.reportInfoTalk(postId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }


}
