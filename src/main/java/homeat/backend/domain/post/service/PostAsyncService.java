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

    @Async
    public void deletePictures(PostType postType,Long id) {
        List<PostPicture> pictures = postPictureRepository.findPostPictureByPostTypeAndMappingId(
                postType, id);
        pictures.forEach(picture -> {
            s3Service.fileDelete(picture.getUrl());
        });
        postPictureRepository.deleteAll(pictures);
    }

    @Async
    public void deleteCommentAndReply(PostType postType, Long id) {
        List<PostComment> postComments = postCommentRepository.findPostCommentByPostTypeAndMappingId(
                postType, id);
        postComments.forEach(postComment -> {
            List<PostReply> replies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                    postType, postComment.getId());
            postReplyRepository.deleteAll(replies);
        });
        postCommentRepository.deleteAll(postComments);
    }

    @Async
    public void deleteLove(PostType postType,Long id) {
        List<PostLove> postLoves = postLoveRepository.findPostLoveByPostTypeAndMappingId(
                postType, id);
        postLoveRepository.deleteAll(postLoves);
    }

    @Async
    public void deleteReport(PostType postType,Long id) {
        List<PostReport> postReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                postType, PostDetailType.POST,
                id);
        List<PostReport> commentReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                postType, PostDetailType.COMMENT,
                id);
        List<PostReport> replyReports = postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingId(
                postType, PostDetailType.REPLY,
                id);
        postReportRepository.deleteAll(postReports);
        postReportRepository.deleteAll(commentReports);
        postReportRepository.deleteAll(replyReports);
    }
}
