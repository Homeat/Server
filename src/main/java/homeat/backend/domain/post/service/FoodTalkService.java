package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodPicture;
import homeat.backend.domain.post.entity.FoodRecipe;
import homeat.backend.domain.post.entity.FoodRecipePicture;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.dto.FoodTalkDTO;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.repository.FoodPictureRepository;
import homeat.backend.domain.post.repository.FoodRecipePictureRepository;
import homeat.backend.domain.post.repository.FoodRecipeRepository;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.global.service.S3Service;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FoodTalkService {

    private final FoodTalkRepository foodTalkRepository;
    private final FoodPictureRepository foodPictureRepository;
    private final FoodRecipeRepository foodRecipeRepository;
    private final FoodRecipePictureRepository foodRecipePictureRepository;
    private final S3Service s3Service;


    // 게시글 작성
    @Transactional
    public ResponseEntity<?> saveFoodTalk(FoodTalkDTO dto) {

        FoodTalk foodTalk = FoodTalk.builder()
                .name(dto.getName())
                .memo(dto.getMemo())
                .tag(dto.getTag())
                .save(Save.저장)
                .build();
        foodTalkRepository.save(foodTalk);


        return ResponseEntity.ok().body(foodTalk);
    }

    @Transactional
    public ResponseEntity<?> uploadImages(Long id, List<MultipartFile> multipartFiles) {
        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);
        postBlankCheck(imgPaths);

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        for (String imgUrl : imgPaths) {
            FoodPicture foodPicture = FoodPicture.builder()
                    .foodTalk(foodTalk)
                    .url(imgUrl)
                    .build();
            foodPictureRepository.save(foodPicture);
        }


        return ResponseEntity.ok(foodTalk.getId() + "번 집밥토크 사진 저장완료");
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

        for (FoodPicture foodPicture : foodTalk.getFoodPictures()) {
            s3Service.fileDelete(foodPicture.getUrl());
        }

        // 레시피 s3 삭제
        if (foodTalk.getFoodRecipes() == null || foodTalk.getFoodRecipes().isEmpty()) {

        } else {
            for (FoodRecipe foodRecipe : foodTalk.getFoodRecipes()) {
                if (foodRecipe.getFoodRecipePictures() == null || foodRecipe.getFoodRecipePictures().isEmpty()) {

                } else {
                    for (FoodRecipePicture foodRecipePicture : foodRecipe.getFoodRecipePictures()) {
                        s3Service.fileDelete(foodRecipePicture.getUrl());
                    }
                }
            }
        }



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

    @Transactional
    public ResponseEntity<?> getFoodTalk(Long id) {


        FoodTalk response = foodTalkRepository.findByFoodTalkId(id);

        response.plusView(response.getView() + 1);

        return ResponseEntity.ok(response);
    }


    public ResponseEntity<?> getFoodTalkLatest(FoodTalkSearchCondition condition, Long lastFoodTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok().body(foodTalkRepository.findByIdLessThanOrderByIdDesc(condition,lastFoodTalkId,pageable));


    }

    public ResponseEntity<?> getFoodTalkOldest(FoodTalkSearchCondition condition, Long OldestFoodTalkId) {


        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok().body(foodTalkRepository.findByIdGreaterThanOrderByIdAsc(condition,OldestFoodTalkId, pageable));
    }

    public ResponseEntity<?> getFoodTalkByLove(FoodTalkSearchCondition condition, Long id, int love) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok().body(foodTalkRepository.findByLoveLessThanOrderByLoveDesc(condition,id,love, pageable));


    }

    public ResponseEntity<?> getFoodTalkByView(FoodTalkSearchCondition condition, Long id, int view) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok().body(foodTalkRepository.findByViewLessThanOrderByViewDesc(condition,id,view, pageable));
    }


    @Transactional
    public ResponseEntity<?> saveRecipe(Long id, String recipe, String ingredient, String tip, List<MultipartFile> files) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        List<FoodRecipe> foodRecipeList = new ArrayList<>();

        FoodRecipe foodRecipe = FoodRecipe.builder()
                .foodTalk(foodTalk)
                .recipe(recipe)
                .ingredient(ingredient)
                .tip(tip)
                .build();

        foodRecipeRepository.save(foodRecipe);

        if (files == null || files.isEmpty()) {

        } else {
            List<String> imgPaths = s3Service.upload(files);
            System.out.println("IMG 경로들 : " + imgPaths);

            for (String imgUrl : imgPaths) {
                FoodRecipePicture foodRecipePicture = FoodRecipePicture.builder()
                        .foodRecipe(foodRecipe)
                        .url(imgUrl)
                        .build();

                foodRecipePictureRepository.save(foodRecipePicture);
            }
        }



        foodRecipeList.add(foodRecipe);

        return ResponseEntity.ok().body(foodRecipeList);
    }


}
