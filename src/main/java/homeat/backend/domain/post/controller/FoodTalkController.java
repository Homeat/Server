package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkSaveDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.service.FoodTalkService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
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
@RequestMapping("/v1/foodTalk")
@RequiredArgsConstructor
public class FoodTalkController {

    private final FoodTalkService foodTalkService;
    private final MemberQueryService memberQueryService;

    /**
     * 집밥토크 저장
     */
    @Operation(summary = "집밥토크 저장 api")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiPayload<FoodResponseDTO.FoodTalkSaveDTO> saveFoodTalk(@RequestBody @Valid FoodRequestDTO.FoodTalkSaveDTO dto,
                                                                    @AuthenticationPrincipal CustomUserDetails authentication) {

        Member member = memberQueryService.mypageMember(authentication.getUserId());
        FoodTalkSaveDTO result = foodTalkService.saveFoodTalk(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }

    /**
     * 집밥토크 사진 업로드
     */
    @Operation(summary = "집밥토크 사진 저장 api")
    @PostMapping(value = "/upload/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<String> uploadImages(@PathVariable("id") Long id,
                                          @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }
        String result = foodTalkService.uploadImages(id, multipartFiles);

        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }


    /**
     * 집밥토크 삭제
     */
    @Operation(summary = "집밥토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ApiPayload<String> deleteFoodTalk(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.deleteFoodTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "집밥토크 게시글 수정 api, 아직 개발 X")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateFoodTalk(@RequestBody @Valid FoodRequestDTO.FoodTalkSaveDTO dto, @PathVariable("id") Long id) {
        return foodTalkService.updateFoodTalk(dto, id);
    }

    /**
     * 집밥토크 조회
     */
    @Operation(summary = "집밥토크 게시글 1개 조회 api")
    @GetMapping("{id}")
    public ApiPayload<FoodResponseDTO.FoodTalkViewDTO> getFoodTalk(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        FoodTalkViewDTO result = foodTalkService.getFoodTalk(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 무한 스크롤 최신순 조회
     */
    @Operation(summary = "집밥토크 최신순 조회 및 검색, lastFoodTalkId 보다 작은 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/latest")
    public ApiPayload<Slice<FoodTalkTotalView>> getFoodTalkLatest(FoodTalkSearchCondition condition, @RequestParam Long lastFoodTalkId) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkLatest(condition, lastFoodTalkId);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK,result);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "집밥토크 오래된 순 조회 및 검색, lastFoodTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/oldest")
    public ApiPayload<Slice<FoodTalkTotalView>> getFoodTalkOldest(FoodTalkSearchCondition condition, @RequestParam Long OldestFoodTalkId) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkOldest(condition, OldestFoodTalkId);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK,result);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "집밥토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/love")
    public ApiPayload<Slice<FoodTalkTotalView>> getFoodTalkByLove(FoodTalkSearchCondition condition, @RequestParam Long id,
                                               @RequestParam int love) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkByLove(condition, id, love);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK,result);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "집밥토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/view")
    public ApiPayload<Slice<FoodTalkTotalView>> getFoodTalkByView(FoodTalkSearchCondition condition, @RequestParam Long id,
                                               @RequestParam int view) {
        Slice<FoodTalkTotalView> result = foodTalkService.getFoodTalkByView(condition, id, view);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK,result);
    }

    /**
     * 레시피 업로드
     */
    @Operation(summary = "집밥토크 레시피 업로드, List 형식입니다!, id는 집밥토크 게시물 id 입니다.")
    @PostMapping(value = "/recipe/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<String> saveRecipe(@PathVariable("id") Long id, @RequestParam("recipe") String recipe,
                                        @RequestParam("ingredient") String ingredient, @RequestParam("tip") String tip,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        String result = foodTalkService.saveRecipe(id, recipe, ingredient, tip, files);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "집밥토크 댓글 작성, id는 집밥토크 게시물 id 입니다.")
    @PostMapping("/comment/{id}")
    public ApiPayload<String> saveComment(@RequestBody @Valid FoodRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {

        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.saveComment(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED,result);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ApiPayload<String> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.deleteComment(commentId, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "집밥토크 대댓글 작성, id는 댓글 아이디입니다.")
    @PostMapping("/reply/{id}")
    public ApiPayload<String> saveReply(@RequestBody @Valid FoodRequestDTO.CommentDTO dto, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.saveReply(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.CREATED, result);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @DeleteMapping("/reply/{id}")
    public ApiPayload<String> deleteReply(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.deleteReply(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 공감하기
     */
    @Operation(summary = "집밥토크 게시물 공감하기 api입니다. id는 집밥토크 게시물 id 입니다")
    @PostMapping("/love/{id}")
    public ApiPayload<String> saveLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.saveLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 공감 취소하기
     */
    @Operation(summary = "집밥토크 게시물 공감 취소하기, id는 집밥토크 게시물 id 입니다")
    @DeleteMapping("/love/{id}")
    public ApiPayload<String> deleteLove(@PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String result = foodTalkService.deleteLove(id, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }
}
