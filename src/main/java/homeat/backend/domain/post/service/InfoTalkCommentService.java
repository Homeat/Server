package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.InfoTalkCommentDTO;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkComment;
import homeat.backend.domain.post.repository.InfoTalkCommentRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InfoTalkCommentService {

    private final InfoTalkCommentRepository infoTalkCommentRepository;
    private final InfoTalkRepository infoTalkRepository;


    @Transactional
    public ResponseEntity<?> saveInfoComment(InfoTalkCommentDTO dto, Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        InfoTalkComment infoTalkComment = InfoTalkComment.builder()
                .infoTalk(infoTalk)
                .content(dto.getContent())
                .build();

        infoTalkCommentRepository.save(infoTalkComment);

        return ResponseEntity.ok("댓글 저장 완료");
    }

    @Transactional
    public ResponseEntity<?> deleteInfoComment(Long id) {

        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 댓글을 찾을 수 없습니다."));

        infoTalkCommentRepository.delete(infoTalkComment);

        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
