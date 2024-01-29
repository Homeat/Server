package homeat.backend.domain.post.service;

import homeat.backend.domain.post.entity.FoodPicture;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.repository.FoodPictureRepository;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.global.service.S3Service;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodTalkService {

    private final FoodTalkRepository foodTalkRepository;
    private final FoodPictureRepository foodPictureRepository;
    private final S3Service s3Service;


    // 게시글 작성
    @Transactional
    public ResponseEntity<?> saveFoodTalk(FoodTalkDTO dto, List<String> imgPaths) {
        postBlankCheck(imgPaths);

        FoodTalk foodTalk = FoodTalk.builder()
                .name(dto.getName())
                .memo(dto.getMemo())
                .tag(dto.getTag())
                .save(Save.저장)
                .build();
        foodTalkRepository.save(foodTalk);

        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            FoodPicture foodPicture = FoodPicture.builder()
                    .foodTalk(foodTalk)
                    .url(imgUrl)
                    .build();
            foodPictureRepository.save(foodPicture);
            imgList.add(foodPicture.getUrl());
        }

        return ResponseEntity.ok("집밥토크 저장완료");
    }
    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new IllegalArgumentException("사진 입력 오류입니다.");
        }
    }
    @Transactional
    public ResponseEntity<?> tempSaveFoodTalk(FoodTalkDTO dto) {
        FoodTalk foodTalk = FoodTalk.builder()
                .name(dto.getName())
                .memo(dto.getMemo())
                .tag(dto.getTag())
                .save(Save.임시저장)
                .build();

        foodTalkRepository.save(foodTalk);

        return ResponseEntity.ok("집밥토크 임시저장완료");
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

        foodTalk.update(dto.getName(), dto.getMemo(), dto.getTag());

        return ResponseEntity.ok(id + " 번 게시글 수정완료");
    }

    public ResponseEntity<?> getFoodTalk(Long id) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        return ResponseEntity.ok(foodTalk);
    }


}
