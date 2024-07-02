package homeat.backend.domain.post.service;

import homeat.backend.domain.post.controller.PostErrorStatus;
import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.FoodRequestDTO.FoodRecipeRequest;
import homeat.backend.domain.post.dto.FoodResponseDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkCommentViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkRecipeViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkReplyViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.entity.FoodRecipe;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.PostComment;
import homeat.backend.domain.post.entity.PostDetailType;
import homeat.backend.domain.post.entity.PostLove;
import homeat.backend.domain.post.entity.PostPicture;
import homeat.backend.domain.post.entity.PostReply;
import homeat.backend.domain.post.entity.PostReport;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.entity.Tag;
import homeat.backend.domain.post.repository.FoodRecipeRepository;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.post.repository.PostCommentRepository;
import homeat.backend.domain.post.repository.PostLoveRepository;
import homeat.backend.domain.post.repository.PostPictureRepository;
import homeat.backend.domain.post.repository.PostReplyRepository;
import homeat.backend.domain.post.repository.PostReportRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.service.S3Service;
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
    private final FoodRecipeRepository foodRecipeRepository;
    private final PostPictureRepository postPictureRepository;
    private final PostLoveRepository postLoveRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostReplyRepository postReplyRepository;
    private final PostReportRepository postReportRepository;
    private final PostAsyncService postAsyncService;
    private final S3Service s3Service;


    // 게시글 작성
    @Transactional
    public void saveFoodTalk(String name, String memo, Tag tag,String ingredient, List<MultipartFile> multipartFiles, Member member, FoodRecipeRequest foodRecipeRequest) {

        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);

        FoodTalk foodTalk = FoodTalk.builder()
                .member(member)
                .name(name)
                .memo(memo)
                .ingredient(ingredient)
                .tag(tag)
                .status(Status.저장)
                .build();
        foodTalkRepository.save(foodTalk);

        imgPaths.forEach(img -> {
            PostPicture postPicture = PostPicture.builder()
                    .postType(PostType.FoodTalk)
                    .mappingId(foodTalk.getId())
                    .url(img)
                    .build();
            postPictureRepository.save(postPicture);
        });
        if (foodRecipeRequest.getFoodRecipeDTOS() != null) {
            foodRecipeRequest.getFoodRecipeDTOS().forEach(foodRecipeDTO -> {
                if (foodRecipeDTO.getRecipe() == null || foodRecipeDTO.getRecipe().isEmpty()) {
                    throw new GeneralException(PostErrorStatus.POST_RECIPE_PAYMENT_REQUIRED);
                }
                if (foodRecipeDTO.getRecipePicture() == null || foodRecipeDTO.getRecipePicture().isEmpty()) {
                    throw new GeneralException(PostErrorStatus.POST_IMAGE_PAYMENT_REQUIRED);
                }


                FoodRecipe foodRecipe = FoodRecipe.builder()
                        .foodTalk(foodTalk)
                        .recipe(foodRecipeDTO.getRecipe())
                        .build();
                foodRecipeRepository.save(foodRecipe);

                String imgUrl = s3Service.singleUpload(foodRecipeDTO.getRecipePicture());

                PostPicture postPicture = PostPicture.builder()
                        .postType(PostType.FoodTalkRecipe)
                        .mappingId(foodRecipe.getId())
                        .url(imgUrl)
                        .build();
                postPictureRepository.save(postPicture);
            });

        }







    }

    @Transactional
    public void deleteFoodTalk(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (member != foodTalk.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        // 집밥토크 사진 삭제
        postAsyncService.deletePictures(PostType.FoodTalk,id);

        // 댓글 대댓글 삭제
        postAsyncService.deleteCommentAndReply(PostType.FoodTalk,id);

        // 좋아요 삭제
        postAsyncService.deleteLove(PostType.FoodTalk,id);

        // 신고 삭제
        postAsyncService.deleteReport(PostType.FoodTalk,id);

        // 레시피 사진 삭제
        if (foodTalk.getFoodRecipes() != null) {
            foodTalk.getFoodRecipes().forEach(foodRecipe -> {
                List<PostPicture> foodRecipePictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(
                        PostType.FoodTalkRecipe, foodRecipe.getId());
                foodRecipePictures.forEach(foodRecipePicture -> {
                    s3Service.fileDelete(foodRecipePicture.getUrl());
                });
                postPictureRepository.deleteAll(foodRecipePictures);
            });
        }

        foodTalkRepository.delete(foodTalk);
    }

    @Transactional
    public FoodResponseDTO.FoodTalkViewDTO getFoodTalk(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));
        if (postLoveRepository.findPostLoveByPostTypeAndMember(PostType.FoodTalk, member).isEmpty()) {
            foodTalk.setLove(false);
        } else {
            foodTalk.setLove(true);
        }

        foodTalk.plusView(foodTalk.getView() + 1);

        // 집밥토크 사진 리스트
        List<String> foodPictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(PostType.FoodTalk,
                        foodTalk.getId()).stream()
                .map(PostPicture::getUrl)
                .toList();

        // 집밥토크 레시피 리스트
        AtomicInteger cnt = new AtomicInteger(1);

        List<FoodResponseDTO.FoodTalkRecipeViewDTO> foodTalkRecipeViewDTOList = foodTalk.getFoodRecipes().stream()
                .map(recipe -> {
                    List<String> recipePictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(PostType.FoodTalkRecipe,
                                    recipe.getId()).stream()
                            .map(PostPicture::getUrl)
                            .collect(Collectors.toList());

                    return FoodTalkRecipeViewDTO.builder()
                            .step(cnt.getAndIncrement())
                            .recipe(recipe.getRecipe())
                            .foodRecipeImages(recipePictures)
                            .build();
                })
                .collect(Collectors.toList());


        // 집밥토크 댓글 리스트
        List<PostComment> foodComments = postCommentRepository.findPostCommentByPostTypeAndMappingId(
                PostType.FoodTalk, foodTalk.getId());
        List<FoodResponseDTO.FoodTalkCommentViewDTO> foodTalkCommentViewDTOList = foodComments.stream()
                .map(foodTalkComment -> {
                    List<PostReply> foodReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                            PostType.FoodTalk, foodTalkComment.getId());
                    List<FoodResponseDTO.FoodTalkReplyViewDTO> foodTalkReplyViewDTOList = foodReplies.stream()
                            .map(foodTalkReply -> FoodTalkReplyViewDTO.builder()
                                    .createdAt(foodTalkReply.getCreatedAt())
                                    .updatedAt(foodTalkReply.getUpdatedAt())
                                    .replyId(foodTalkReply.getId())
                                    .replyNickName(foodTalkReply.getMember().getNickname())
                                    .content(foodTalkReply.getContent())
                                    .status(Status.저장)
                                    .build())
                            .collect(Collectors.toList());

                    return FoodTalkCommentViewDTO.builder()
                            .createdAt(foodTalkComment.getCreatedAt())
                            .updatedAt(foodTalkComment.getUpdatedAt())
                            .commentId(foodTalkComment.getId())
                            .commentNickName(foodTalkComment.getMember().getNickname())
                            .content(foodTalkComment.getContent())
                            .status(Status.저장)
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
                .ingredient(foodTalk.getIngredient())
                .tag(foodTalk.getTag())
                .love(foodTalk.getLove())
                .view(foodTalk.getView())
                .commentNumber(foodTalk.getCommentNumber())
                .setLove(foodTalk.getSetLove())
                .status(foodTalk.getStatus())
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
    public void saveComment(FoodRequestDTO.CommentDTO dto, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        PostComment postComment = PostComment.builder()
                .member(member)
                .postType(PostType.FoodTalk)
                .mappingId(foodTalk.getId())
                .content(dto.getContent())
                .status(Status.저장)
                .build();

        postCommentRepository.save(postComment);

        int commentNum = foodTalkRepository.countTotalCommentNumber(dto.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(postComment.getId()).intValue();



        foodTalk.updateCommentSize(commentNum + replyNum);


    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {

        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (member != postComment.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        postCommentRepository.delete(postComment);

        // 연관된 대댓글 삭제
        List<PostReply> foodReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                PostType.FoodTalk, commentId);
        postReplyRepository.deleteAll(foodReplies);

        FoodTalk foodTalk = foodTalkRepository.findById(postComment.getMappingId()).orElseThrow();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(commentId).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void saveReply(FoodRequestDTO.CommentDTO dto, Member member) {

        PostComment postComment = postCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        PostReply postReply = PostReply.builder()
                .postType(PostType.FoodTalk)
                .member(member)
                .content(dto.getContent())
                .status(Status.저장)
                .mappingId(postComment.getId())
                .build();

        postReplyRepository.save(postReply);

        FoodTalk foodTalk = foodTalkRepository.findById(postComment.getMappingId()).orElseThrow();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(postComment.getId()).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void deleteReply(Long id, Member member) {

        PostReply postReply = postReplyRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (member != postReply.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        postReplyRepository.delete(postReply);

        FoodTalk foodTalk = foodTalkRepository.findById(postCommentRepository.findById(postReply.getMappingId()).orElseThrow().getMappingId()).orElseThrow();

        int commentNum = foodTalkRepository.countTotalCommentNumber(foodTalk.getId()).intValue();
        int replyNum = foodTalkRepository.countTotalReplyNumber(postReply.getMappingId()).intValue();

        foodTalk.updateCommentSize(commentNum + replyNum);

    }

    @Transactional
    public void saveLove(Long id, Member member) {

        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (foodTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_SET_LOVE_BAD_REQUEST);
        }

        PostLove postLove = PostLove.builder()
                .postType(PostType.FoodTalk)
                .mappingId(foodTalk.getId())
                .member(member)
                .build();


        foodTalk.plusLove(foodTalk.getLove() + 1);
        foodTalk.setLove(true);

        postLoveRepository.save(postLove);
    }

    @Transactional
    public void deleteLove(Long id, Member member) {
        FoodTalk foodTalk = foodTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (!foodTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_CANCEL_LOVE_BAD_REQUEST);
        }

        PostLove postLove = postLoveRepository.findPostLoveByPostTypeAndMember(PostType.FoodTalk, member).orElseThrow();

        foodTalk.setLove(false);
        foodTalk.plusLove(foodTalk.getLove() - 1);

        postLoveRepository.delete(postLove);
    }

    @Transactional
    public void reportFoodTalk(Long postId, Member member) {
        FoodTalk foodTalk = foodTalkRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.FoodTalk,
                PostDetailType.POST,postId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.FoodTalk)
                    .postDetailType(PostDetailType.POST)
                    .mappingId(postId)
                    .member(member)
                    .build();
            postReportRepository.save(postReport);

            foodTalk.plusReport(foodTalk.getReportNumber() + 1);

            if (foodTalk.getReportNumber() >= 10) {
                foodTalk.reported();
            }
        }

    }

    @Transactional
    public void reportFoodTalkComment(Long commentId, Member member) {
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.FoodTalk,
                PostDetailType.COMMENT, commentId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_COMMENT_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.FoodTalk)
                    .postDetailType(PostDetailType.COMMENT)
                    .mappingId(commentId)
                    .member(member)
                    .build();
            postReportRepository.save(postReport);

            postComment.plusReport(postComment.getReportNumber() + 1);

            if (postComment.getReportNumber() >= 10) {
                postComment.reported();
            }
        }
    }

    @Transactional
    public void reportFoodTalkReply(Long replyId, Member member) {
        PostReply postReply = postReplyRepository.findById(replyId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.FoodTalk,
                PostDetailType.REPLY, replyId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_REPLY_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.FoodTalk)
                    .postDetailType(PostDetailType.REPLY)
                    .mappingId(replyId)
                    .member(member)
                    .build();
            postReportRepository.save(postReport);

            postReply.plusReport(postReply.getReportNumber() + 1);

            if (postReply.getReportNumber() >= 10) {
                postReply.reported();
            }
        }


    }
}
