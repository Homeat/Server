package homeat.backend.domain.post.service;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodTalkService {

    private final FoodTalkRepository foodTalkRepository;


    @Transactional
    public ResponseEntity<?> saveFoodTalk(FoodTalkDTO dto) {
        FoodTalk foodTalk = FoodTalk.builder()
                .name(dto.getName())
                .ingredient(dto.getIngredient())
                .memo(dto.getMemo())
                .tag(dto.getTag())
                .build();

        foodTalkRepository.save(foodTalk);

        return ResponseEntity.ok("음식토크 저장완료");
    }

    @Transactional
    public ResponseEntity<?> deleteFoodTalk(Long id) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        foodTalkRepository.delete(foodTalk);




        return ResponseEntity.ok(id + " 번 게시글 삭제완료");
    }


    @Transactional
    public ResponseEntity<?> updateFoodTalk(FoodTalkDTO dto, Long id) {
        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        foodTalk.update(dto.getName(), dto.getIngredient(), dto.getMemo(), dto.getTag());

        return ResponseEntity.ok(id + " 번 게시글 수정완료");
    }
}
