package homeat.backend.controller.post;

import homeat.backend.dto.post.FoodTalkDTO;
import homeat.backend.service.post.FoodTalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/foodTalk")
@RequiredArgsConstructor
public class FoodTalkController {

    private final FoodTalkService foodTalkService;

    /**
     * 음식토크 저장
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveFoodTalk(@RequestBody FoodTalkDTO dto) {
        return foodTalkService.saveFoodTalk(dto);
    }

    /**
     * 음식토크 삭제
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteFoodTalk(@PathVariable("id") Long id) {
        return foodTalkService.deleteFoodTalk(id);
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateFoodTalk(@RequestBody FoodTalkDTO dto, @PathVariable("id") Long id) {
        return foodTalkService.updateFoodTalk(dto, id);
    }
}
