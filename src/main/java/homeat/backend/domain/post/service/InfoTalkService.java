package homeat.backend.domain.post.service;

import homeat.backend.domain.post.controller.PostErrorStatus;
import homeat.backend.domain.post.dto.InfoRequestDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkCommentViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkReplyViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.InfoHashTag;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.PostComment;
import homeat.backend.domain.post.entity.PostDetailType;
import homeat.backend.domain.post.entity.PostLove;
import homeat.backend.domain.post.entity.PostPicture;
import homeat.backend.domain.post.entity.PostReply;
import homeat.backend.domain.post.entity.PostReport;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.repository.InfoHashTagRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
import homeat.backend.domain.post.repository.PostCommentRepository;
import homeat.backend.domain.post.repository.PostLoveRepository;
import homeat.backend.domain.post.repository.PostPictureRepository;
import homeat.backend.domain.post.repository.PostReplyRepository;
import homeat.backend.domain.post.repository.PostReportRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.service.S3Service;
import java.util.List;
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
public class InfoTalkService {

    private final InfoTalkRepository infoTalkRepository;
    private final InfoHashTagRepository infoHashTagRepository;
    private final PostPictureRepository postPictureRepository;
    private final PostLoveRepository postLoveRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostReplyRepository postReplyRepository;
    private final PostReportRepository postReportRepository;
    private final PostAsyncService postAsyncService;
    private final S3Service s3Service;

    // 정보토크 게시글 작성
    @Transactional
    public void saveInfoTalk(String title,String content,List<String> tags,List<MultipartFile> multipartFiles, Member member) {
        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);

        InfoTalk infoTalk = InfoTalk.builder()
                .title(title)
                .content(content)
                .status(Status.저장)
                .member(member)
                .build();

        infoTalkRepository.save(infoTalk);

        // 해시태그 리스트
        if (tags != null) {
            tags.stream()
                    .map(tag -> {
                        InfoHashTag infoHashTag = InfoHashTag.builder()
                                .infoTalk(infoTalk)
                                .tag(tag)
                                .build();
                        return infoHashTagRepository.save(infoHashTag);
                    })
                    .forEach(savedInfoHashTag ->{});
        }




        imgPaths.forEach(img -> {
            PostPicture postPicture = PostPicture.builder()
                    .postType(PostType.InfoTalk)
                    .mappingId(infoTalk.getId())
                    .url(img)
                    .build();
            postPictureRepository.save(postPicture);
        });

    }

    @Transactional
    public void deleteInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (member != infoTalk.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        // 집밥토크 사진 삭제
        postAsyncService.deletePictures(PostType.InfoTalk,id);

        // 댓글 대댓글 삭제
        postAsyncService.deleteCommentAndReply(PostType.InfoTalk,id);

        // 좋아요 삭제
        postAsyncService.deleteLove(PostType.InfoTalk,id);

        // 신고 삭제
        postAsyncService.deleteReport(PostType.InfoTalk,id);



        infoTalkRepository.delete(infoTalk);
    }

    @Transactional
    // 정보토크 게시글 1개 조회
    public InfoResponseDTO.InfoTalkViewDTO getInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (postLoveRepository.findPostLoveByPostTypeAndMember(PostType.InfoTalk, member).isEmpty()) {
            infoTalk.setLove(false);
        } else {
            infoTalk.setLove(true);
        }

        infoTalk.plusView(infoTalk.getView() + 1);

        // 해시태그 리스트
        List<String> infoTags = infoTalk.getInfoHashTags().stream()
                .map(InfoHashTag::getTag)
                .collect(Collectors.toList());


        // 정보토크 사진 리스트
        List<String> infoImages = postPictureRepository.findPostPictureByPostTypeAndMappingId(PostType.InfoTalk,
                        infoTalk.getId()).stream()
                .map(PostPicture::getUrl)
                .collect(Collectors.toList());

        // 정보토크 댓글, 대댓글 DTO 생성
        List<PostComment> infoComments = postCommentRepository.findPostCommentByPostTypeAndMappingId(
                PostType.InfoTalk, infoTalk.getId());

        List<InfoResponseDTO.InfoTalkCommentViewDTO> infoTalkCommentViewDTOList = infoComments.stream()
                .map(infoTalkComment -> {
                    List<PostReply> infoReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                            PostType.InfoTalk, infoTalkComment.getId());
                    List<InfoResponseDTO.InfoTalkReplyViewDTO> infoTalkReplyViewDTOList = infoReplies.stream()
                            .map(infoTalkReply -> InfoTalkReplyViewDTO.builder()
                                    .createdAt(infoTalkReply.getCreatedAt())
                                    .updatedAt(infoTalkReply.getUpdatedAt())
                                    .replyId(infoTalkReply.getId())
                                    .replyNickName(infoTalkReply.getMember().getNickname())
                                    .content(infoTalkReply.getContent())
                                    .build())
                            .collect(Collectors.toList());

                    return InfoTalkCommentViewDTO.builder()
                            .createdAt(infoTalkComment.getCreatedAt())
                            .updatedAt(infoTalkComment.getUpdatedAt())
                            .commentId(infoTalkComment.getId())
                            .commentNickName(infoTalkComment.getMember().getNickname())
                            .content(infoTalkComment.getContent())
                            .infoTalkReplies(infoTalkReplyViewDTOList)
                            .build();
                })
                .collect(Collectors.toList());


        return InfoTalkViewDTO.builder()
                .createdAt(infoTalk.getCreatedAt())
                .updatedAt(infoTalk.getUpdatedAt())
                .id(infoTalk.getId())
                .postNickName(infoTalk.getMember().getNickname())
                .title(infoTalk.getTitle())
                .content(infoTalk.getContent())
                .tags(infoTags)
                .love(infoTalk.getLove())
                .view(infoTalk.getView())
                .commentNumber(infoTalk.getCommentNumber())
                .setLove(infoTalk.getSetLove())
                .status(infoTalk.getStatus())
                .infoPictureImages(infoImages)
                .infoTalkComments(infoTalkCommentViewDTOList)
                .build();

    }

    public Slice<InfoTalkTotalView> getInfoTalkLatest(InfoTalkSearchCondition condition, Long lastInfoTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return infoTalkRepository.findByIdLessThanOrderByIdDesc(condition,
                lastInfoTalkId, pageable);
    }

    public Slice<InfoTalkTotalView> getInfoTalkOldest(InfoTalkSearchCondition condition, Long oldestInfoTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return infoTalkRepository.findByIdGreaterThanOrderByIdAsc(condition, oldestInfoTalkId, pageable);
    }

    public Slice<InfoTalkTotalView> getInfoTalkByLove(InfoTalkSearchCondition condition, Long id, int love) {

        Pageable pageable = PageRequest.of(0, 6);

        return infoTalkRepository.findByLoveLessThanOrderByLoveDesc(condition, id, love, pageable);
    }

    public Slice<InfoTalkTotalView> getInfoTalkByView(InfoTalkSearchCondition condition, Long id, int view) {

        Pageable pageable = PageRequest.of(0, 6);

        return infoTalkRepository.findByViewLessThanOrderByViewDesc(condition, id, view, pageable);
    }


    @Transactional
    public void saveComment(InfoRequestDTO.CommentDTO dto, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        PostComment postComment = PostComment.builder()
                .member(member)
                .postType(PostType.InfoTalk)
                .mappingId(infoTalk.getId())
                .content(dto.getContent())
                .status(Status.저장)
                .build();

        postCommentRepository.save(postComment);

        int commentNum = infoTalkRepository.countTotalCommentNumber(dto.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(postComment.getId()).intValue();



        infoTalk.updateCommentSize(commentNum + replyNum);

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
        List<PostReply> infoReplies = postReplyRepository.findPostRepliesByPostTypeAndMappingId(
                PostType.InfoTalk, commentId);
        postReplyRepository.deleteAll(infoReplies);

        InfoTalk infoTalk = infoTalkRepository.findById(postComment.getMappingId()).orElseThrow();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(commentId).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);

    }

    @Transactional
    public void saveReply(InfoRequestDTO.CommentDTO dto, Member member) {
        PostComment postComment = postCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        PostReply postReply = PostReply.builder()
                .postType(PostType.InfoTalk)
                .member(member)
                .content(dto.getContent())
                .status(Status.저장)
                .mappingId(postComment.getId())
                .build();

        postReplyRepository.save(postReply);

        InfoTalk infoTalk = infoTalkRepository.findById(postComment.getMappingId()).orElseThrow();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(postComment.getId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);


    }

    @Transactional
    public void deleteReply(Long id, Member member) {
        PostReply postReply = postReplyRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (member != postReply.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        postReplyRepository.delete(postReply);

        InfoTalk infoTalk = infoTalkRepository.findById(
                postCommentRepository.findById(postReply.getMappingId()).orElseThrow().getMappingId()).orElseThrow();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(postReply.getMappingId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void saveLove(Long id, Member member) {
        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (infoTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_SET_LOVE_BAD_REQUEST);
        }

        PostLove postLove = PostLove.builder()
                .postType(PostType.InfoTalk)
                .mappingId(infoTalk.getId())
                .member(member)
                .build();


        infoTalk.plusLove(infoTalk.getLove() + 1);
        infoTalk.setLove(true);

        postLoveRepository.save(postLove);
    }

    @Transactional
    public void deleteLove(Long id, Member member) {


        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (!infoTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_CANCEL_LOVE_BAD_REQUEST);
        }

        PostLove postLove = postLoveRepository.findPostLoveByPostTypeAndMember(PostType.InfoTalk, member).orElseThrow();

        infoTalk.setLove(false);
        infoTalk.plusLove(infoTalk.getLove() - 1);

        postLoveRepository.delete(postLove);

    }

    @Transactional
    public void reportInfoTalk(Long postId, Member member) {
        InfoTalk infoTalk = infoTalkRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.InfoTalk,
                PostDetailType.POST,postId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.InfoTalk)
                    .postDetailType(PostDetailType.POST)
                    .mappingId(postId)
                    .member(member)
                    .build();
            postReportRepository.save(postReport);

            infoTalk.plusReport(infoTalk.getReportNumber() + 1);

            if (infoTalk.getReportNumber() >= 10) {
                infoTalk.reported();
            }
        }

    }

    @Transactional
    public void reportInfoTalkComment(Long commentId, Member member) {
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.InfoTalk,
                PostDetailType.COMMENT, commentId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_COMMENT_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.InfoTalk)
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
    public void reportInfoTalkReply(Long replyId, Member member) {
        PostReply postReply = postReplyRepository.findById(replyId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (postReportRepository.findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType.InfoTalk,
                PostDetailType.REPLY, replyId, member).isPresent()) {
            throw new GeneralException(PostErrorStatus.POST_REPLY_REPORT_BAD_REQUEST);
        } else {
            PostReport postReport = PostReport.builder()
                    .postType(PostType.InfoTalk)
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
