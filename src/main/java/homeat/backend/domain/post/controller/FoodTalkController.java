package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.service.FoodTalkService;
import homeat.backend.global.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @Operation(summary = "집밥토크 저장 api, 스웨거 사용 X")
    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveFoodTalk(@RequestPart("content") FoodTalkDTO dto,
                                          @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {

        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }
        return foodTalkService.saveFoodTalk(dto, multipartFiles);
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
    public ResponseEntity<?> getFoodTalk(@PathVariable("id")Long id) {
        return foodTalkService.getFoodTalk(id);
    }



}
