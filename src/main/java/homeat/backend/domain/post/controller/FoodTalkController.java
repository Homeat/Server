package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.service.FoodTalkService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    /**
     * 집밥토크 저장
     */
    @Operation(summary = "집밥토크 저장 api")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveFoodTalk(@RequestBody @Valid FoodTalkDTO dto) {

        return foodTalkService.saveFoodTalk(dto);
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
     * 집밥토크 임시저장
     */
    @Operation(summary = "집밥토크 임시저장 api")
    @PostMapping("/tempSave")
    public ResponseEntity<?> tempSaveFoodTalk(@RequestBody @Valid FoodTalkDTO dto) {
        return foodTalkService.tempSaveFoodTalk(dto);
    }

    /**
     * 집밥토크 삭제
     */
    @Operation(summary = "집밥토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteFoodTalk(@PathVariable("id") Long id) {
        return foodTalkService.deleteFoodTalk(id);
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "집밥토크 게시글 수정 api")
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
}
