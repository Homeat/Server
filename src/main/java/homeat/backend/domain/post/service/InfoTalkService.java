package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.repository.InfoTalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InfoTalkService {

    private final InfoTalkRepository infoTalkRepository;


    @Transactional
    public ResponseEntity<?> saveInfoTalk(InfoTalkDTO dto) {

        InfoTalk infoTalk = InfoTalk.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        infoTalkRepository.save(infoTalk);


        return ResponseEntity.ok("정보토크 저장완료");
    }

    @Transactional
    public ResponseEntity<?> deleteInfoTalk(Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        infoTalkRepository.delete(infoTalk);

        return ResponseEntity.ok(id + " 번 게시글 삭제완료");
    }

    @Transactional
    public ResponseEntity<?> updateInfoTalk(InfoTalkDTO dto, Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        infoTalk.update(dto.getTitle(), dto.getContent());

        return ResponseEntity.ok(id + " 번 게시글 수정완료");
    }
}
