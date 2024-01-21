package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.service.InfoTalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/infoTalk")
@RequiredArgsConstructor
public class InfoTalkController {

    private final InfoTalkService infoTalkService;

    /**
     * 정보토크 저장
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveInfoTalk(@RequestBody InfoTalkDTO dto) {
        return infoTalkService.saveInfoTalk(dto);
    }


    /**
     * 정보토크 삭제
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteInfoTalk(@PathVariable("id") Long id) {
        return infoTalkService.deleteInfoTalk(id);
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateInfoTalk(@RequestBody InfoTalkDTO dto, @PathVariable("id") Long id) {
        return infoTalkService.updateInfoTalk(dto, id);
    }
}
