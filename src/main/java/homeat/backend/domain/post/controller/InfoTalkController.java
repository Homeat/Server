package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.service.InfoTalkService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/infoTalk")
@RequiredArgsConstructor
public class InfoTalkController {

    private final InfoTalkService infoTalkService;

    /**
     * 정보토크 저장
     */
    @Operation(summary = "정보토크 저장 api, , 스웨거 사용 X")
    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> saveInfoTalk(@RequestPart("content") InfoTalkDTO dto,
                                          @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {

        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }
        return infoTalkService.saveInfoTalk(dto, multipartFiles);
    }

    /**
     * 정보토크 임시저장
     */
    @Operation(summary = "정보토크 임시저장 api")
    @PostMapping("/tempSave")
    public ResponseEntity<?> tempSaveInfoTalk(@RequestBody @Valid InfoTalkDTO dto) {
        return infoTalkService.tempSaveInfoTalk(dto);
    }


    /**
     * 정보토크 삭제
     */
    @Operation(summary = "정보토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteInfoTalk(@PathVariable("id") Long id) {
        return infoTalkService.deleteInfoTalk(id);
    }

    /**
     * 게시글 수정
     */
    @Operation(summary = "정보토크 게시글 수정 api")
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateInfoTalk(@RequestBody @Valid InfoTalkDTO dto, @PathVariable("id") Long id) {
        return infoTalkService.updateInfoTalk(dto, id);
    }

    /**
     * 정보토크 조회
     */
    @Operation(summary = "정보토크 게시글 1개 조회 api")
    @GetMapping("/{id}")
    public ResponseEntity<?> getInfoTalk(@PathVariable("id") Long id) {
        return infoTalkService.getInfoTalk(id);
    }
}
