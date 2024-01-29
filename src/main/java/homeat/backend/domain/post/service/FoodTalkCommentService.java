package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.FoodTalkCommentDTO;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.FoodTalkComment;
import homeat.backend.domain.post.repository.FoodTalkCommentRepository;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodTalkCommentService {

    private final FoodTalkCommentRepository foodTalkCommentRepository;
    private final FoodTalkRepository foodTalkRepository;



    @Transactional
    public ResponseEntity<?> saveFoodComment(FoodTalkCommentDTO dto, Long id) {


        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));


        FoodTalkComment foodTalkComment = FoodTalkComment.builder()
                .content(dto.getContent())
                .foodTalk(foodTalk)
                .build();

        foodTalkCommentRepository.save(foodTalkComment);

        return ResponseEntity.ok("댓글 저장 완료");

    }

    @Transactional
    public ResponseEntity<?> deleteFoodComment(Long id) {

        FoodTalkComment foodTalkComment = foodTalkCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 댓글을 찾을 수 없습니다."));

        foodTalkCommentRepository.delete(foodTalkComment);

        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
