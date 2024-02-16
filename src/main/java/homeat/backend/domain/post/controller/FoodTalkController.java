package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.CommentDTO;
import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.service.FoodTalkService;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> saveFoodTalk(@RequestBody @Valid FoodTalkDTO dto, Authentication authentication) {

        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return foodTalkService.saveFoodTalk(dto, member);
    }

    /**
     * 집밥토크 사진 업로드
     */
    @Operation(summary = "집밥토크 사진 저장 api")
    @PostMapping(value = "/upload/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long id,
                                          @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }

        return foodTalkService.uploadImages(id, multipartFiles);
    }


    /**
     * 집밥토크 삭제
     */
    @Operation(summary = "집밥토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteFoodTalk(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));

        return foodTalkService.deleteFoodTalk(id, member);
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "집밥토크 게시글 수정 api, 아직 개발 X")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateFoodTalk(@RequestBody @Valid FoodTalkDTO dto, @PathVariable("id") Long id) {
        return foodTalkService.updateFoodTalk(dto, id);
    }

    /**
     * 집밥토크 조회
     */
    @Operation(summary = "집밥토크 게시글 1개 조회 api")
    @GetMapping("{id}")
    public ResponseEntity<?> getFoodTalk(@PathVariable("id") Long id) {
        return foodTalkService.getFoodTalk(id);
    }

    /**
     * 무한 스크롤 최신순 조회
     */
    @Operation(summary = "집밥토크 최신순 조회 및 검색, lastFoodTalkId 보다 작은 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/latest")
    public ResponseEntity<?> getFoodTalkLatest(FoodTalkSearchCondition condition, @RequestParam Long lastFoodTalkId) {
        return foodTalkService.getFoodTalkLatest(condition, lastFoodTalkId);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "집밥토크 오래된 순 조회 및 검색, lastFoodTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/oldest")
    public ResponseEntity<?> getFoodTalkOldest(FoodTalkSearchCondition condition, @RequestParam Long OldestFoodTalkId) {
        return foodTalkService.getFoodTalkOldest(condition, OldestFoodTalkId);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "집밥토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/love")
    public ResponseEntity<?> getFoodTalkByLove(FoodTalkSearchCondition condition, @RequestParam Long id,
                                               @RequestParam int love) {
        return foodTalkService.getFoodTalkByLove(condition, id, love);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "집밥토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/view")
    public ResponseEntity<?> getFoodTalkByView(FoodTalkSearchCondition condition, @RequestParam Long id,
                                               @RequestParam int view) {
        return foodTalkService.getFoodTalkByView(condition, id, view);
    }

    /**
     * 레시피 업로드
     */
    @Operation(summary = "집밥토크 레시피 업로드, List 형식입니다!, id는 집밥토크 게시물 id 입니다.")
    @PostMapping(value = "/recipe/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveRecipe(@PathVariable("id") Long id, @RequestParam("recipe") String recipe,
                                        @RequestParam("ingredient") String ingredient, @RequestParam("tip") String tip,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        return foodTalkService.saveRecipe(id, recipe, ingredient, tip, files);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "집밥토크 댓글 작성, id는 집밥토크 게시물 id 입니다.")
    @PostMapping("/comment/{id}")
    public ResponseEntity<?> saveComment(@RequestBody @Valid CommentDTO dto, Authentication authentication) {

        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return foodTalkService.saveComment(dto, member);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return foodTalkService.deleteComment(commentId, member);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "집밥토크 대댓글 작성, id는 댓글 아이디입니다.")
    @PostMapping("/reply/{id}")
    public ResponseEntity<?> saveReply(@RequestBody @Valid CommentDTO dto, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return foodTalkService.saveReply(dto, member);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @DeleteMapping("/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return foodTalkService.deleteReply(id, member);
    }
}
