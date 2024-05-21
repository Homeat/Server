package homeat.backend.domain.post.service;

import homeat.backend.domain.post.controller.PostErrorStatus;
import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkCommentViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkRecipeViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkReplyViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.entity.FoodPicture;
import homeat.backend.domain.post.entity.FoodRecipe;
import homeat.backend.domain.post.entity.FoodRecipePicture;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.FoodTalkComment;
import homeat.backend.domain.post.entity.FoodTalkLove;
import homeat.backend.domain.post.entity.FoodTalkReply;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.entity.Tag;
import homeat.backend.domain.post.repository.FoodLoveRepository;
import homeat.backend.domain.post.repository.FoodPictureRepository;
import homeat.backend.domain.post.repository.FoodRecipePictureRepository;
import homeat.backend.domain.post.repository.FoodRecipeRepository;
import homeat.backend.domain.post.repository.FoodTalkCommentRepository;
import homeat.backend.domain.post.repository.FoodTalkReplyRepository;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.service.S3Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final FoodTalkCommentRepository foodTalkCommentRepository;
    private final FoodTalkReplyRepository foodTalkReplyRepository;
    private final FoodLoveRepository foodLoveRepository;
    private final S3Service s3Service;


    // 게시글 작성
    @Transactional
    public Long saveFoodTalk(String name, String memo, Tag tag, List<MultipartFile> multipartFiles, Member member) {

        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);
        postBlankCheck(imgPaths);

        FoodTalk foodTalk = FoodTalk.builder()
                .member(member)
                .name(name)
                .memo(memo)
                .tag(tag)
                .save(Save.저장)
                .build();
        foodTalkRepository.save(foodTalk);

        for (String imgUrl : imgPaths) {
            FoodPicture foodPicture = FoodPicture.builder()
                    .foodTalk(foodTalk)
                    .url(imgUrl)
                    .build();
            foodPictureRepository.save(foodPicture);
        }

        return foodTalk.getId();

    }

    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new GeneralException(PostErrorStatus.POST_IMAGE_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteFoodTalk(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (member != foodTalk.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        for (FoodPicture foodPicture : foodTalk.getFoodPictures()) {
            s3Service.fileDelete(foodPicture.getUrl());
        }

        // 레시피 s3 삭제
        if (foodTalk.getFoodRecipes() != null) {
            for (FoodRecipe foodRecipe : foodTalk.getFoodRecipes()) {
                if (foodRecipe.getFoodRecipePictures() != null) {
                    for (FoodRecipePicture foodRecipePicture : foodRecipe.getFoodRecipePictures()) {
                        s3Service.fileDelete(foodRecipePicture.getUrl());
                    }
                }
            }

        }



        foodTalkRepository.delete(foodTalk);
    }


//    @Transactional
//    public ResponseEntity<?> updateFoodTalk(FoodRequestDTO.FoodTalkSaveDTO dto, Long id) {
//        FoodTalk foodTalk = foodTalkRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));
//
//        foodTalk.update(dto.getName(), dto.getMemo(), dto.getTag());
//
//        return ResponseEntity.ok(id + " 번 게시글 수정완료");
//    }

    @Transactional
    public FoodResponseDTO.FoodTalkViewDTO getFoodTalk(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));
        if (foodLoveRepository.findByFoodTalkAndMember(foodTalk, member) == null) {
            foodTalk.setLove(false);
        } else {
            foodTalk.setLove(true);
        }

        foodTalk.plusView(foodTalk.getView() + 1);

        // 집밥토크 사진 리스트
        List<String> foodPictures = foodTalk.getFoodPictures().stream()
                .map(FoodPicture::getUrl)
                .toList();

        // 집밥토크 레시피 리스트
        AtomicInteger cnt = new AtomicInteger(1);

        List<FoodResponseDTO.FoodTalkRecipeViewDTO> foodTalkRecipeViewDTOList = foodTalk.getFoodRecipes().stream()
                .map(recipe -> {
                    List<String> recipePictures = recipe.getFoodRecipePictures().stream()
                            .map(FoodRecipePicture::getUrl)
                            .collect(Collectors.toList());

                    return FoodTalkRecipeViewDTO.builder()
                            .step(cnt.getAndIncrement())
                            .recipe(recipe.getRecipe())
                            .ingredient(recipe.getIngredient())
                            .tip(recipe.getTip())
                            .foodRecipeImages(recipePictures)
                            .build();
                })
                .collect(Collectors.toList());


        // 집밥토크 댓글 리스트
        List<FoodResponseDTO.FoodTalkCommentViewDTO> foodTalkCommentViewDTOList = foodTalk.getFoodTalkComments().stream()
                .map(foodTalkComment -> {
                    List<FoodResponseDTO.FoodTalkReplyViewDTO> foodTalkReplyViewDTOList = foodTalkComment.getReplyList().stream()
                            .map(foodTalkReply -> FoodTalkReplyViewDTO.builder()
                                    .createdAt(foodTalkReply.getCreatedAt())
                                    .updatedAt(foodTalkReply.getUpdatedAt())
                                    .replyId(foodTalkReply.getId())
                                    .replyNickName(foodTalkReply.getMember().getNickname())
                                    .content(foodTalkReply.getContent())
                                    .build())
                            .collect(Collectors.toList());

                    return FoodTalkCommentViewDTO.builder()
                            .createdAt(foodTalkComment.getCreatedAt())
                            .updatedAt(foodTalkComment.getUpdatedAt())
                            .commentId(foodTalkComment.getId())
                            .commentNickName(foodTalkComment.getMember().getNickname())
                            .content(foodTalkComment.getContent())
                            .foodTalkReplies(foodTalkReplyViewDTOList)
                            .build();
                })
                .collect(Collectors.toList());


        return FoodTalkViewDTO.builder()
                .createdAt(foodTalk.getCreatedAt())
                .updatedAt(foodTalk.getUpdatedAt())
                .id(foodTalk.getId())
                .postNickName(member.getNickname())
                .name(foodTalk.getName())
                .memo(foodTalk.getMemo())
                .tag(foodTalk.getTag())
                .love(foodTalk.getLove())
                .view(foodTalk.getView())
                .commentNumber(foodTalk.getCommentNumber())
                .setLove(foodTalk.getSetLove())
                .foodPictureImages(foodPictures)
                .foodTalkRecipes(foodTalkRecipeViewDTOList)
                .foodTalkComments(foodTalkCommentViewDTOList)
                .build();
    }


    public Slice<FoodTalkTotalView> getFoodTalkLatest(FoodTalkSearchCondition condition, Long lastFoodTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return foodTalkRepository.findByIdLessThanOrderByIdDesc(condition, lastFoodTalkId, pageable);


    }

    public Slice<FoodTalkTotalView> getFoodTalkOldest(FoodTalkSearchCondition condition, Long OldestFoodTalkId) {


        Pageable pageable = PageRequest.of(0, 6);


        return foodTalkRepository.findByIdGreaterThanOrderByIdAsc(condition, OldestFoodTalkId, pageable);
    }

    public Slice<FoodTalkTotalView> getFoodTalkByLove(FoodTalkSearchCondition condition, Long id, int love) {

        Pageable pageable = PageRequest.of(0, 6);

        return foodTalkRepository.findByLoveLessThanOrderByLoveDesc(condition,id,love, pageable);


    }

    public Slice<FoodTalkTotalView> getFoodTalkByView(FoodTalkSearchCondition condition, Long id, int view) {

        Pageable pageable = PageRequest.of(0, 6);

        return foodTalkRepository.findByViewLessThanOrderByViewDesc(condition,id,view, pageable);
    }


    @Transactional
    public void saveRecipe(Long id, String recipe, String ingredient, String tip, List<MultipartFile> files) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));


        FoodRecipe foodRecipe = FoodRecipe.builder()
                .foodTalk(foodTalk)
                .recipe(recipe)
                .ingredient(ingredient)
                .tip(tip)
                .build();

        foodRecipeRepository.save(foodRecipe);

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


    @Transactional
    public void saveComment(FoodRequestDTO.CommentDTO dto, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        FoodTalkComment foodTalkComment = FoodTalkComment.builder()
                .member(member)
                .foodTalk(foodTalk)
                .content(dto.getContent())
                .build();

        foodTalkCommentRepository.save(foodTalkComment);

        int commentNum = foodTalkRepository.countTotalCommentNumber(dto.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(foodTalkComment.getId()).intValue();



        foodTalk.updateCommentSize(commentNum + replyNum);


    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {

        FoodTalkComment foodTalkComment = foodTalkCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (member != foodTalkComment.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        foodTalkCommentRepository.delete(foodTalkComment);

        FoodTalk foodTalk = foodTalkComment.getFoodTalk();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(commentId).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void saveReply(FoodRequestDTO.CommentDTO dto, Member member) {

        FoodTalkComment foodTalkComment = foodTalkCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        FoodTalkReply foodTalkReply = FoodTalkReply.builder()
                .foodTalkComment(foodTalkComment)
                .member(member)
                .content(dto.getContent())
                .build();

        foodTalkReplyRepository.save(foodTalkReply);

        FoodTalk foodTalk = foodTalkComment.getFoodTalk();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(foodTalkComment.getId()).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void deleteReply(Long id, Member member) {

        FoodTalkReply foodTalkReply = foodTalkReplyRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (member != foodTalkReply.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        foodTalkReplyRepository.delete(foodTalkReply);

        FoodTalk foodTalk = foodTalkReply.getFoodTalkComment().getFoodTalk();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(foodTalkReply.getFoodTalkComment().getId()).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);

    }

    @Transactional
    public void saveLove(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (foodTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_SET_LOVE_BAD_REQUEST);
        }

        FoodTalkLove foodTalkLove = FoodTalkLove.builder()
                .foodTalk(foodTalk)
                .member(member)
                .build();

        foodTalk.plusLove(foodTalk.getLove() + 1);
        foodTalk.setLove(true);

        foodLoveRepository.save(foodTalkLove);
    }

    @Transactional
    public void deleteLove(Long id, Member member) {
        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (!foodTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_CANCEL_LOVE_BAD_REQUEST);
        }

        FoodTalkLove foodTalkLove = foodLoveRepository.findByFoodTalkAndMember(foodTalk, member);

        foodTalk.setLove(false);
        foodTalk.plusLove(foodTalk.getLove() - 1);

        foodLoveRepository.delete(foodTalkLove);
    }
}
