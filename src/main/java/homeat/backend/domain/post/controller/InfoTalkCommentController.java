package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.InfoTalkCommentDTO;
import homeat.backend.domain.post.service.InfoTalkCommentService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/infoTalk/comment")
@RequiredArgsConstructor
public class InfoTalkCommentController {

    private final InfoTalkCommentService infoTalkCommentService;

    /**
     * 댓글 작성
     */
    @Operation(summary = "정보토크 댓글 작성 api")
    @PostMapping("/save/{id}")
    public ResponseEntity<?> saveInfoComment(@RequestBody @Valid InfoTalkCommentDTO dto, @PathVariable("id") Long id) {
        return infoTalkCommentService.saveInfoComment(dto, id);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "정보토크 댓글 삭제 api")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInfoComment(@PathVariable("id") Long id) {
        return infoTalkCommentService.deleteInfoComment(id);
    }
}
