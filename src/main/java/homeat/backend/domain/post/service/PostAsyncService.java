package homeat.backend.domain.post.service;

import homeat.backend.domain.post.entity.FoodRecipe;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.PostComment;
import homeat.backend.domain.post.entity.PostDetailType;
import homeat.backend.domain.post.entity.PostLove;
import homeat.backend.domain.post.entity.PostPicture;
import homeat.backend.domain.post.entity.PostReply;
import homeat.backend.domain.post.entity.PostReport;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.post.repository.PostCommentRepository;
import homeat.backend.domain.post.repository.PostLoveRepository;
import homeat.backend.domain.post.repository.PostPictureRepository;
import homeat.backend.domain.post.repository.PostReplyRepository;
import homeat.backend.domain.post.repository.PostReportRepository;
import homeat.backend.global.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAsyncService {
    private final S3Service s3Service;
    private final PostPictureRepository postPictureRepository;
    private final PostLoveRepository postLoveRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostReplyRepository postReplyRepository;
    private final PostReportRepository postReportRepository;
    private final FoodTalkRepository foodTalkRepository;

    @Async
    public void deleteFoodPictures(Long foodTalkId) {
        List<PostPicture> foodPictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(
                PostType.FoodTalk, foodTalkId);
        foodPictures.forEach(foodPicture -> {
            s3Service.fileDelete(foodPicture.getUrl());
        });
        postPictureRepository.deleteAll(foodPictures);
    }

    @Async
    public void deleteFoodTalkCommentAndReply(Long foodTalkId) {
        List<PostComment> postComments = postCommentRepository.findPostCommentByPostTypeAndMappingId(
                PostType.FoodTalk, foodTalkId);
        postComments.forEach(postComment -> {
            List<PostReply> foodReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                    PostType.FoodTalk, postComment.getId());
            postReplyRepository.deleteAll(foodReplies);
        });
        postCommentRepository.deleteAll(postComments);
    }

    @Async
    public void deleteFoodTalkLove(Long foodTalkId) {
        List<PostLove> postLoves = postLoveRepository.findPostLoveByPostTypeAndMappingId(
                PostType.FoodTalk, foodTalkId);
        postLoveRepository.deleteAll(postLoves);
    }

    @Async
    public void deleteFoodTalkReport(Long foodTalkId) {
        List<PostReport> postReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.FoodTalk, PostDetailType.POST,
                foodTalkId);
        List<PostReport> commentReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.FoodTalk, PostDetailType.COMMENT,
                foodTalkId);
        List<PostReport> replyReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.FoodTalk, PostDetailType.REPLY,
                foodTalkId);
        postReportRepository.deleteAll(postReports);
        postReportRepository.deleteAll(commentReports);
        postReportRepository.deleteAll(replyReports);
    }

    @Async
    public void deleteInfoPictures(Long id) {
        List<PostPicture> infoTalkPictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(
                PostType.InfoTalk, id);
        infoTalkPictures.forEach(infoTalkPicture -> {
            s3Service.fileDelete(infoTalkPicture.getUrl());
        });
        postPictureRepository.deleteAll(infoTalkPictures);
    }

    @Async
    public void deleteInfoTalkCommentAndReply(Long id) {
        List<PostComment> postComments = postCommentRepository.findPostCommentByPostTypeAndMappingId(
                PostType.InfoTalk, id);
        postComments.forEach(postComment -> {
            List<PostReply> infoReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                    PostType.InfoTalk, postComment.getId());
            postReplyRepository.deleteAll(infoReplies);
        });
        postCommentRepository.deleteAll(postComments);

    }
    @Async
    public void deleteInfoTalkLove(Long id) {
        List<PostLove> postLoves = postLoveRepository.findPostLoveByPostTypeAndMappingId(
                PostType.FoodTalk, id);
        postLoveRepository.deleteAll(postLoves);
    }
    @Async
    public void deleteInfoTalkReport(Long id) {
        List<PostReport> postReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.InfoTalk, PostDetailType.POST,
                id);
        List<PostReport> commentReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.InfoTalk, PostDetailType.COMMENT,
                id);
        List<PostReport> replyReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                PostType.InfoTalk, PostDetailType.REPLY,
                id);
        postReportRepository.deleteAll(postReports);
        postReportRepository.deleteAll(commentReports);
        postReportRepository.deleteAll(replyReports);

    }
}
