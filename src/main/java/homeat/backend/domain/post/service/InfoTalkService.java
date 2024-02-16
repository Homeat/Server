package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.CommentDTO;
import homeat.backend.domain.post.dto.InfoHashTagDTO;
import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodPicture;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.FoodTalkComment;
import homeat.backend.domain.post.entity.FoodTalkReply;
import homeat.backend.domain.post.entity.InfoHashTag;
import homeat.backend.domain.post.entity.InfoPicture;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkComment;
import homeat.backend.domain.post.entity.InfoTalkReply;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.repository.InfoHashTagRepository;
import homeat.backend.domain.post.repository.InfoPictureRepository;
import homeat.backend.domain.post.repository.InfoTalkCommentRepository;
import homeat.backend.domain.post.repository.InfoTalkReplyRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
import homeat.backend.domain.user.entity.Member;
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
public class InfoTalkService {

    private final InfoTalkRepository infoTalkRepository;
    private final InfoPictureRepository infoPictureRepository;
    private final InfoHashTagRepository infoHashTagRepository;
    private final InfoTalkCommentRepository infoTalkCommentRepository;
    private final InfoTalkReplyRepository infoTalkReplyRepository;
    private final S3Service s3Service;

    // 정보토크 게시글 작성
    @Transactional
    public ResponseEntity<?> saveInfoTalk(InfoTalkDTO dto) {

        InfoTalk infoTalk = InfoTalk.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .save(Save.저장)
                .build();

        infoTalkRepository.save(infoTalk);

        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tag : dto.getTags()) {
                InfoHashTag infoHashTag = InfoHashTag.builder()
                        .infoTalk(infoTalk)
                        .tag(tag)
                        .build();
                infoHashTagRepository.save(infoHashTag);
            }

        }


        return ResponseEntity.ok().body(infoTalk);
    }

    @Transactional
    public ResponseEntity<?> uploadImages(Long id, List<MultipartFile> multipartFiles) {
        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);
        postBlankCheck(imgPaths);

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        for (String imgUrl : imgPaths) {
            InfoPicture infoPicture = InfoPicture.builder()
                    .infoTalk(infoTalk)
                    .url(imgUrl)
                    .build();
            infoPictureRepository.save(infoPicture);
        }


        return ResponseEntity.ok(infoTalk.getId() + "번 정보토크 사진 저장완료");
    }


    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new IllegalArgumentException("사진 입력 오류입니다.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteInfoTalk(Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        for (InfoPicture infoPicture : infoTalk.getInfoPictures()) {
            s3Service.fileDelete(infoPicture.getUrl());
        }

        infoTalkRepository.delete(infoTalk);

        return ResponseEntity.ok(id + " 번 게시글 삭제완료");
    }

    @Transactional
    public ResponseEntity<?> updateInfoTalk(InfoTalkDTO dto, Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        infoTalk.update(dto.getTitle(), dto.getContent());

        return ResponseEntity.ok(id + " 번 게시글 수정완료");
    }

    @Transactional
    // 정보토크 게시글 1개 조회
    public ResponseEntity<?> getInfoTalk(Long id) {

        InfoTalk response = infoTalkRepository.findByInfoTalkId(id);

        response.plusView(response.getView() + 1);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getInfoTalkLatest(InfoTalkSearchCondition condition, Long lastInfoTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok()
                .body(infoTalkRepository.findByIdLessThanOrderByIdDesc(condition, lastInfoTalkId, pageable));
    }

    public ResponseEntity<?> getInfoTalkOldest(InfoTalkSearchCondition condition, Long oldestInfoTalkId) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok()
                .body(infoTalkRepository.findByIdGreaterThanOrderByIdAsc(condition, oldestInfoTalkId, pageable));
    }

    public ResponseEntity<?> getInfoTalkByLove(InfoTalkSearchCondition condition, Long id, int love) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok()
                .body(infoTalkRepository.findByLoveLessThanOrderByLoveDesc(condition, id, love, pageable));
    }

    public ResponseEntity<?> getInfoTalkByView(InfoTalkSearchCondition condition, Long id, int view) {

        Pageable pageable = PageRequest.of(0, 6);

        return ResponseEntity.ok()
                .body(infoTalkRepository.findByViewLessThanOrderByViewDesc(condition, id, view, pageable));
    }


    @Transactional
    public ResponseEntity<?> saveComment(CommentDTO dto, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(dto.getId() + " 번의 게시글을 찾을 수 없습니다."));

        InfoTalkComment infoTalkComment = InfoTalkComment.builder()
                .member(member)
                .infoTalk(infoTalk)
                .content(dto.getContent())
                .build();

        infoTalkCommentRepository.save(infoTalkComment);

        int commentNum = infoTalkRepository.countTotalCommentNumber(dto.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkComment.getId()).intValue();



        infoTalk.updateCommentSize(commentNum + replyNum);

        return ResponseEntity.ok().body(infoTalkComment);
    }

    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId, Member member) {
        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(commentId + " 번의 댓글을 찾을 수 없습니다."));

        if (member != infoTalkComment.getMember()) {
            throw new IllegalArgumentException("댓글 작성자가 달라 삭제할 수 없습니다");
        }

        infoTalkCommentRepository.delete(infoTalkComment);

        InfoTalk infoTalk = infoTalkComment.getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(commentId).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);

        return ResponseEntity.ok(commentId + "번 댓글 삭제 완료");
    }

    @Transactional
    public ResponseEntity<?> saveReply(CommentDTO dto, Member member) {
        InfoTalkComment infoTalkComment = infoTalkCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(dto.getId() + " 번의 댓글을 찾을 수 없습니다."));

        InfoTalkReply infoTalkReply = InfoTalkReply.builder()
                .infoTalkComment(infoTalkComment)
                .member(member)
                .content(dto.getContent())
                .build();

        infoTalkReplyRepository.save(infoTalkReply);

        InfoTalk infoTalk = infoTalkComment.getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkComment.getId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);



        return ResponseEntity.ok().body(infoTalkReply);
    }

    @Transactional
    public ResponseEntity<?> deleteReply(Long id, Member member) {
        InfoTalkReply infoTalkReply = infoTalkReplyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 댓글을 찾을 수 없습니다."));

        if (member != infoTalkReply.getMember()) {
            throw new IllegalArgumentException("댓글 작성자가 달라 삭제할 수 없습니다");
        }

        infoTalkReplyRepository.delete(infoTalkReply);

        InfoTalk infoTalk = infoTalkReply.getInfoTalkComment().getInfoTalk();

        int commentNum = infoTalkRepository.countTotalCommentNumber(infoTalk.getId()).intValue();
        int replyNum = infoTalkRepository.countTotalReplyNumber(infoTalkReply.getInfoTalkComment().getId()).intValue();

        infoTalk.updateCommentSize(commentNum + replyNum);

        return ResponseEntity.ok(id + "번 댓글 삭제 완료");
    }
}
