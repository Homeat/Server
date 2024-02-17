package homeat.backend.domain.post.controller;

import homeat.backend.domain.post.dto.CommentDTO;
import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.service.InfoTalkService;
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
@RequestMapping("/v1/infoTalk")
@RequiredArgsConstructor
public class InfoTalkController {

    private final InfoTalkService infoTalkService;
    private final MemberQueryService memberQueryService;

    /**
     * 정보토크 저장
     */
    @Operation(summary = "정보토크 내용 저장 api")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveInfoTalk(@RequestBody InfoTalkDTO dto, Authentication authentication) {

        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));

        return infoTalkService.saveInfoTalk(dto, member);
    }

    /**
     * 정보토크 사진 업로드
     */
    @Operation(summary = "정보토크 사진 저장 api")
    @PostMapping(value = "/upload/images/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long id,@RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("사진이 없습니다");
        }

        return infoTalkService.uploadImages(id, multipartFiles);
    }


    /**
     * 정보토크 삭제
     */
    @Operation(summary = "정보토크 삭제 api")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteInfoTalk(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.deleteInfoTalk(id, member);
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
    public ResponseEntity<?> getInfoTalk(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.getInfoTalk(id, member);
    }

    /**
     * 무한 스크롤 최신순 조회
     */
    @Operation(summary = "정보 최신순 조회 및 검색, lastInfoTalkId 보다 작은 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/latest")
    public ResponseEntity<?> getInfoTalkLatest(InfoTalkSearchCondition condition, @RequestParam Long lastInfoTalkId) {
        return infoTalkService.getInfoTalkLatest(condition,lastInfoTalkId);
    }

    /**
     * 무한 스크롤 오래된 순 조회
     */
    @Operation(summary = "정보토크 오래된 순 조회 및 검색, lastInfoTalkId 보다 큰 6개 게시물을 보여줍니다.")
    @GetMapping("/posts/oldest")
    public ResponseEntity<?> getInfoTalkOldest(InfoTalkSearchCondition condition,@RequestParam Long oldestInfoTalkId) {
        return infoTalkService.getInfoTalkOldest(condition,oldestInfoTalkId);
    }

    /**
     * 무한 스크롤 공감 순 조회
     */
    @Operation(summary = "정보토크 공감 순 조회 및 검색, 공감 내림차순 6개 게시물을 보여줍니다. 만약 공감이 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/love")
    public ResponseEntity<?> getInfoTalkByLove(InfoTalkSearchCondition condition, @RequestParam Long id, @RequestParam int love) {
        return infoTalkService.getInfoTalkByLove(condition,id,love);
    }

    /**
     * 무한 스크롤 조회 순 조회
     */
    @Operation(summary = "정보토크 조회 순 조회 및 검색, 조회 내림차순 6개 게시물을 보여줍니다. 만약 조회수 같을 시 ID 내림차순입니다.")
    @GetMapping("/posts/view")
    public ResponseEntity<?> getInfoTalkByView(InfoTalkSearchCondition condition,@RequestParam Long id,@RequestParam int view) {
        return infoTalkService.getInfoTalkByView(condition,id,view);
    }

    /**
     * 댓글 작성
     */
    @Operation(summary = "정보토크 댓글 작성, id는 정보토크 게시물 id 입니다.")
    @PostMapping("/comment/{id}")
    public ResponseEntity<?> saveComment(@RequestBody @Valid CommentDTO dto, Authentication authentication) {

        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.saveComment(dto, member);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제 api입니다. id는 댓글 아이디입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.deleteComment(commentId, member);
    }

    /**
     * 대댓글 작성
     */
    @Operation(summary = "정보토크 대댓글 작성, id는 댓글 아이디입니다.")
    @PostMapping("/reply/{id}")
    public ResponseEntity<?> saveReply(@RequestBody @Valid CommentDTO dto, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.saveReply(dto, member);
    }

    /**
     * 대댓글 삭제
     */
    @Operation(summary = "대댓글 삭제, id는 대댓글 아이디입니다")
    @DeleteMapping("/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.deleteReply(id, member);
    }

    /**
     * 공감하기
     */
    @Operation(summary = "정보토크 게시물 공감하기 api입니다. id는 정보토크 게시물 id 입니다")
    @PostMapping("/love/{id}")
    public ResponseEntity<?> saveLove(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.saveLove(id, member);
    }

    /**
     * 공감 취소하기
     */
    @Operation(summary = "정보토크 게시물 공감 취소하기, id는 정보토크 게시물 id 입니다")
    @DeleteMapping("/love/{id}")
    public ResponseEntity<?> deleteLove(@PathVariable("id") Long id, Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return infoTalkService.deleteLove(id, member);
    }


}
