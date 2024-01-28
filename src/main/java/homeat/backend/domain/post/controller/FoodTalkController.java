package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.service.FoodTalkService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/foodTalk")
@RequiredArgsConstructor
public class FoodTalkController {

    private final FoodTalkService foodTalkService;

    /**
     * 집밥토크 저장
     */
    @Operation(summary = "집밥토크 저장 api")
    @PostMapping("/save")
    public ResponseEntity<?> saveFoodTalk(@RequestBody @Valid FoodTalkDTO dto) {
        return foodTalkService.saveFoodTalk(dto);
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
    @Operation(summary = "집밥토크 조회 api")
    @GetMapping("{id}")
    public ResponseEntity<?> getFoodTalk(@PathVariable("id")Long id) {
        return foodTalkService.getFoodTalk(id);
    }



}
