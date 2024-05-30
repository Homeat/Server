package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.FoodRequestDTO.FoodRecipeRequest;
import homeat.backend.domain.post.dto.FoodResponseDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.entity.Tag;
import homeat.backend.domain.post.service.FoodTalkService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import homeat.backend.global.payload.SlicePayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/foodTalk")
@RequiredArgsConstructor
public class FoodTalkController {

    private final FoodTalkService foodTalkService;
    private final MemberQueryService memberQueryService;

    /**
     * 집밥토크 저장
     */
    @Operation(summary = "집밥토크 및 레시피 통합 저장 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4005 : 사진 입력 오류", content = {@Content()}),
            @ApiResponse(responseCode = "402", description = "POST_4020 : NAME이 입력되지 않았습니다\n\nPOST_4021 : MEMO가 입력되지 않았습니다\n\nPOST_4022 : TAG가 입력되지 않았습니다\n\nPOST_4023 : IMAGE가 입력되지 않았습니다\n\nPOST_4027 : RECIPE가 입력되지 않았습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<?> saveFoodTalk(@RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "memo", required = false) String memo,
                                         @RequestParam(value = "tag", required = false) Tag tag,
                                         @ModelAttribute List<MultipartFile> foodPictures,
                                         @ModelAttribute FoodRecipeRequest foodRecipeRequest,
                                         @AuthenticationPrincipal CustomUserDetails authentication) {
        if (name == null || name.isEmpty()) {
            throw new GeneralException(PostErrorStatus.POST_NAME_PAYMENT_REQUIRED);
        }
        if (memo == null || memo.isEmpty()) {
            throw new GeneralException(PostErrorStatus.POST_MEMO_PAYMENT_REQUIRED);
        }
        if (tag == null) {
            throw new GeneralException(PostErrorStatus.POST_TAG_PAYMENT_REQUIRED);
        }
        if (foodPictures == null || foodPictures.isEmpty()) {
            throw new GeneralException(PostErrorStatus.POST_IMAGE_PAYMENT_REQUIRED);
        }
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.saveFoodTalk(name, memo, tag, foodPictures, member,foodRecipeRequest);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }


    /**
     * 집밥토크 삭제
     */
    @Operation(summary = "집밥토크 삭제 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "POST_4010 : 작성자가 아니라 삭제할 권한이 없습니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @DeleteMapping("{id}")
    public ApiPayload<?> deleteFoodTalk(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.deleteFoodTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 게시글 수정
     */
//    @Operation(summary = "집밥토크 게시글 수정 api, 아직 개발 X")
//    @PatchMapping("/update/{id}")
//    public ResponseEntity<?> updateFoodTalk(@RequestBody @Valid FoodRequestDTO.FoodTalkSaveDTO dto, @PathVariable("id") Long id) {
//        return foodTalkService.updateFoodTalk(dto, id);
//    }

    /**
     * 집밥토크 조회
     */
    @Operation(summary = "집밥토크 게시글 1개 조회 api")
    @GetMapping("{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    public ApiPayload<FoodResponseDTO.FoodTalkViewDTO> getFoodTalk(@PathVariable("id") Long id,
                                                                   @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        FoodTalkViewDTO result = foodTalkService.getFoodTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 최신순 조회
     */
    @Operation(summary = "집밥토크 최신순 조회 및 검색, lastFoodTalkId 보다 작은 6개 게시물을 보여줍니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("/posts/latest")
    public SlicePayload<FoodTalkTotalView> getFoodTalkLatest(FoodTalkSearchCondition condition,
                                                             @RequestParam Long lastFoodTalkId) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkLatest(condition, lastFoodTalkId);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "집밥토크 오래된 순 조회 및 검색, lastFoodTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("/posts/oldest")
    public SlicePayload<FoodTalkTotalView> getFoodTalkOldest(FoodTalkSearchCondition condition,
                                                             @RequestParam Long OldestFoodTalkId) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkOldest(condition, OldestFoodTalkId);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "집밥토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("/posts/love")
    public SlicePayload<FoodTalkTotalView> getFoodTalkByLove(FoodTalkSearchCondition condition, @RequestParam Long id,
                                                             @RequestParam int love) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkByLove(condition, id, love);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "집밥토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @GetMapping("/posts/view")
    public SlicePayload<FoodTalkTotalView> getFoodTalkByView(FoodTalkSearchCondition condition, @RequestParam Long id,
                                                             @RequestParam int view) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkByView(condition, id, view);
        return SlicePayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "집밥토크 댓글 작성, id는 집밥토크 게시물 id 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/comment")
    public ApiPayload<?> saveComment(@RequestBody @Valid FoodRequestDTO.CommentDTO dto,
                                     @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.saveComment(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "POST_4010 : 작성자가 아니라 삭제할 권한이 없습니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다\n\nPOST_4042 : 댓글이 존재하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @DeleteMapping("/comment/{commentId}")
    public ApiPayload<?> deleteComment(@PathVariable("commentId") Long commentId,
                                       @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.deleteComment(commentId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "집밥토크 대댓글 작성, id는 댓글 아이디입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4042 : 댓글이 존재하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/reply")
    public ApiPayload<?> saveReply(@RequestBody @Valid FoodRequestDTO.CommentDTO dto,
                                   @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.saveReply(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, null);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "POST_4010 : 작성자가 아니라 삭제할 권한이 없습니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4042 : 댓글이 존재하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @DeleteMapping("/reply/{replyId}")
    public ApiPayload<?> deleteReply(@PathVariable("replyId") Long id,
                                     @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.deleteReply(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 공감하기
     */
    @Operation(summary = "집밥토크 게시물 공감하기 api입니다. id는 집밥토크 게시물 id 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4000 : 이미 좋아요를 누른 글입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/love/{id}")
    public ApiPayload<?> saveLove(@PathVariable("id") Long id,
                                  @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.saveLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 공감 취소하기
     */
    @Operation(summary = "집밥토크 게시물 공감 취소하기, id는 집밥토크 게시물 id 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4001 : 이미 좋아요를 취소한 글입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @DeleteMapping("/love/{id}")
    public ApiPayload<?> deleteLove(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.deleteLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 집밥토크 신고하기
     */
    @Operation(summary = "집밥토크 게시물 신고하기, postId는 집밥토크 게시물 id입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4002 : 이미 신고한 글입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4040 : 존재하지 않는 게시물입니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/report/post/{postId})")
    public ApiPayload<?> reportFoodTalk(@PathVariable("postId") Long postId,
                                        @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.reportFoodTalk(postId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 집밥토크 댓글 신고하기
     */
    @Operation(summary = "집밥토크 댓글 신고하기, commentId는 집밥토크 댓글 id입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4003 : 이미 신고한 댓글입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4042 : 댓글이 존재하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/report/comment/{commentId})")
    public ApiPayload<?> reportFoodTalkComment(@PathVariable("commentId") Long commentId,
                                        @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.reportFoodTalkComment(commentId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }

    /**
     * 집밥토크 대댓글 신고하기
     */
    @Operation(summary = "집밥토크 대댓글 신고하기, replyId는 집밥토크 대댓글 id입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "COMMON_400 : 잘못된 요청\n\nPOST_4004 : 이미 신고한 대댓글입니다", content = {@Content()}),
            @ApiResponse(responseCode = "404", description = "POST_4043 : 대댓글이 존재하지 않습니다", content = {@Content()}),
            @ApiResponse(responseCode = "500", description = "COMMON_500 : 서버 에러, 관리자에게 문의하세요", content = {@Content()})
    })
    @PostMapping("/report/reply/{replyId})")
    public ApiPayload<?> reportFoodTalkReply(@PathVariable("replyId") Long replyId,
                                               @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        foodTalkService.reportFoodTalkReply(replyId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, null);
    }


}
