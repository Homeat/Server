package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodTalkCommentDTO;
import homeat.backend.domain.post.service.FoodTalkCommentService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/foodTalk/comment")
@RequiredArgsConstructor
public class FoodTalkCommentController {

    private final FoodTalkCommentService foodTalkCommentService;

    /**
     * 댓글 작성
     */
    @Operation(summary = "집밥토크 댓글 작성 api")
    @PostMapping("/save/{id}")
    public ResponseEntity<?> saveFoodComment(@RequestBody @Valid FoodTalkCommentDTO dto, @PathVariable("id") Long id) {
        return foodTalkCommentService.saveFoodComment(dto, id);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "집밥토크 댓글 삭제 api")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFoodComment(@PathVariable("id") Long id) {
        return foodTalkCommentService.deleteFoodComment(id);
    }
}
