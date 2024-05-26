package homeat.backend.domain.post.service;

import homeat.backend.domain.post.controller.PostErrorStatus;
import homeat.backend.domain.post.dto.InfoRequestDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkCommentViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkReplyViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.entity.FoodTalkCommentReport;
import homeat.backend.domain.post.entity.FoodTalkReply;
import homeat.backend.domain.post.entity.FoodTalkReplyReport;
import homeat.backend.domain.post.entity.InfoHashTag;
import homeat.backend.domain.post.entity.InfoPicture;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkComment;
import homeat.backend.domain.post.entity.InfoTalkCommentReport;
import homeat.backend.domain.post.entity.InfoTalkLove;
import homeat.backend.domain.post.entity.InfoTalkReply;
import homeat.backend.domain.post.entity.InfoTalkReplyReport;
import homeat.backend.domain.post.entity.InfoTalkReport;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.repository.InfoHashTagRepository;
import homeat.backend.domain.post.repository.InfoPictureRepository;
import homeat.backend.domain.post.repository.InfoTalkCommentReportRepository;
import homeat.backend.domain.post.repository.InfoTalkCommentRepository;
import homeat.backend.domain.post.repository.InfoTalkLoveRepository;
import homeat.backend.domain.post.repository.InfoTalkReplyReportRepository;
import homeat.backend.domain.post.repository.InfoTalkReplyRepository;
import homeat.backend.domain.post.repository.InfoTalkReportRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
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
    private final InfoPictureRepository infoPictureRepository;
    private final InfoHashTagRepository infoHashTagRepository;
    private final InfoTalkCommentRepository infoTalkCommentRepository;
    private final InfoTalkReplyRepository infoTalkReplyRepository;
    private final InfoTalkLoveRepository infoTalkLoveRepository;
    private final InfoTalkReportRepository infoTalkReportRepository;
    private final InfoTalkCommentReportRepository infoTalkCommentReportRepository;
    private final InfoTalkReplyReportRepository infoTalkReplyReportRepository;
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




        for (String imgUrl : imgPaths) {
            InfoPicture infoPicture = InfoPicture.builder()
                    .infoTalk(infoTalk)
                    .url(imgUrl)
                    .build();
            infoPictureRepository.save(infoPicture);
        }

    }

    @Transactional
    public void deleteInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (member != infoTalk.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        for (InfoPicture infoPicture : infoTalk.getInfoPictures()) {
            s3Service.fileDelete(infoPicture.getUrl());
        }

        infoTalkRepository.delete(infoTalk);
    }

//    @Transactional
//    public ResponseEntity<?> updateInfoTalk(InfoRequestDTO.InfoTalkDTO dto, Long id) {
//
//        InfoTalk infoTalk = infoTalkRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));
//
//        infoTalk.update(dto.getTitle(), dto.getContent());
//
//        return ResponseEntity.ok(id + " 번 게시글 수정완료");
//    }

    @Transactional
    // 정보토크 게시글 1개 조회
    public InfoResponseDTO.InfoTalkViewDTO getInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (infoTalkLoveRepository.findByInfoTalkAndMember(infoTalk, member) == null) {
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
        List<String> infoImages = infoTalk.getInfoPictures().stream()
                .map(InfoPicture::getUrl)
                .collect(Collectors.toList());

        // 정보토크 댓글, 대댓글 DTO 생성
        List<InfoResponseDTO.InfoTalkCommentViewDTO> infoTalkCommentViewDTOList = infoTalk.getInfoTalkComments().stream()
                .map(infoTalkComment -> {
                    List<InfoResponseDTO.InfoTalkReplyViewDTO> infoTalkReplyViewDTOList = infoTalkComment.getReplyList().stream()
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

        InfoTalkComment infoTalkComment = InfoTalkComment.builder()
                .member(member)
                .infoTalk(infoTalk)
                .content(dto.getContent())
                .status(Status.저장)
                .build();

        infoTalkCommentRepository.save(infoTalkComment);

        int commentNum = infoTalkRepository.countTotalCommentNumber(dto.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkComment.getId()).intValue();



        infoTalk.updateCommentSize(commentNum + replyNum);

    }

    @Transactional
    public void deleteComment(Long commentId, Member member) {
        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (member != infoTalkComment.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        infoTalkCommentRepository.delete(infoTalkComment);

        InfoTalk infoTalk = infoTalkComment.getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(commentId).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);

    }

    @Transactional
    public void saveReply(InfoRequestDTO.CommentDTO dto, Member member) {
        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        InfoTalkReply infoTalkReply = InfoTalkReply.builder()
                .infoTalkComment(infoTalkComment)
                .member(member)
                .content(dto.getContent())
                .status(Status.저장)
                .build();

        infoTalkReplyRepository.save(infoTalkReply);

        InfoTalk infoTalk = infoTalkComment.getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkComment.getId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);


    }

    @Transactional
    public void deleteReply(Long id, Member member) {
        InfoTalkReply infoTalkReply = infoTalkReplyRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (member != infoTalkReply.getMember()) {
            throw new GeneralException(PostErrorStatus.POST_DELETE_UNAUTHORIZED);
        }

        infoTalkReplyRepository.delete(infoTalkReply);

        InfoTalk infoTalk = infoTalkReply.getInfoTalkComment().getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkReply.getInfoTalkComment().getId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);
    }

    @Transactional
    public void saveLove(Long id, Member member) {
        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (infoTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_SET_LOVE_BAD_REQUEST);
        }

        InfoTalkLove infoTalkLove = InfoTalkLove.builder()
                .infoTalk(infoTalk)
                .member(member)
                .build();

        infoTalk.plusLove(infoTalk.getLove() + 1);
        infoTalk.setLove(true);

        infoTalkLoveRepository.save(infoTalkLove);
    }

    @Transactional
    public void deleteLove(Long id, Member member) {


        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (!infoTalk.getSetLove()) {
            throw new GeneralException(PostErrorStatus.POST_CANCEL_LOVE_BAD_REQUEST);
        }

        InfoTalkLove infoTalkLove = infoTalkLoveRepository.findByInfoTalkAndMember(infoTalk, member);

        infoTalk.setLove(false);
        infoTalk.plusLove(infoTalk.getLove() - 1);

        infoTalkLoveRepository.delete(infoTalkLove);

    }

    @Transactional
    public void reportInfoTalk(Long postId, Member member) {
        InfoTalk infoTalk = infoTalkRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_NOT_FOUND));

        if (infoTalkReportRepository.findByInfoTalkAndMember(infoTalk, member) != null) {
            throw new GeneralException(PostErrorStatus.POST_REPORT_BAD_REQUEST);
        } else {
            InfoTalkReport infoTalkReport = InfoTalkReport.builder()
                    .infoTalk(infoTalk)
                    .member(member)
                    .build();
            infoTalkReportRepository.save(infoTalkReport);

            infoTalk.plusReport(infoTalk.getReportNumber() + 1);

            if (infoTalk.getReportNumber() >= 10) {
                infoTalk.reported();
            }
        }

    }

    @Transactional
    public void reportInfoTalkComment(Long commentId, Member member) {
        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_COMMENT_NOT_FOUND));

        if (infoTalkCommentReportRepository.findByInfoTalkCommentAndMember(infoTalkComment, member) != null) {
            throw new GeneralException(PostErrorStatus.POST_COMMENT_REPORT_BAD_REQUEST);
        } else {
            InfoTalkCommentReport infoTalkCommentReport = InfoTalkCommentReport.builder()
                    .infoTalkComment(infoTalkComment)
                    .member(member)
                    .build();
            infoTalkCommentReportRepository.save(infoTalkCommentReport);

            infoTalkComment.plusReport(infoTalkComment.getReportNumber() + 1);

            if (infoTalkComment.getReportNumber() >= 10) {
                infoTalkComment.reported();
            }
        }
    }



    @Transactional
    public void reportInfoTalkReply(Long replyId, Member member) {

        InfoTalkReply infoTalkReply = infoTalkReplyRepository.findById(replyId)
                .orElseThrow(() -> new GeneralException(PostErrorStatus.POST_REPLY_NOT_FOUND));

        if (infoTalkReplyReportRepository.findByInfoTalkReplyAndMember(infoTalkReply, member) != null) {
            throw new GeneralException(PostErrorStatus.POST_REPLY_REPORT_BAD_REQUEST);
        } else {
            InfoTalkReplyReport infoTalkReplyReport = InfoTalkReplyReport.builder()
                    .infoTalkReply(infoTalkReply)
                    .member(member)
                    .build();

            infoTalkReplyReportRepository.save(infoTalkReplyReport);

            infoTalkReply.plusReport(infoTalkReply.getReportNumber() + 1);

            if (infoTalkReply.getReportNumber() >= 10) {
                infoTalkReply.reported();
            }
        }

    }
}
