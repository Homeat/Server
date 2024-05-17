package homeat.backend.domain.post.service;

import homeat.backend.domain.address.repository.AddressRepository;
import homeat.backend.domain.post.dto.FoodRequestDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkCommentViewDTO;
import homeat.backend.domain.post.dto.FoodResponseDTO.FoodTalkReplyViewDTO;
import homeat.backend.domain.post.dto.InfoRequestDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkCommentViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkReplyViewDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkSaveDTO;
import homeat.backend.domain.post.dto.InfoResponseDTO.InfoTalkViewDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.entity.FoodTalkComment;
import homeat.backend.domain.post.entity.FoodTalkReply;
import homeat.backend.domain.post.entity.InfoHashTag;
import homeat.backend.domain.post.entity.InfoPicture;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkComment;
import homeat.backend.domain.post.entity.InfoTalkLove;
import homeat.backend.domain.post.entity.InfoTalkReply;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.repository.InfoHashTagRepository;
import homeat.backend.domain.post.repository.InfoPictureRepository;
import homeat.backend.domain.post.repository.InfoTalkCommentRepository;
import homeat.backend.domain.post.repository.InfoTalkLoveRepository;
import homeat.backend.domain.post.repository.InfoTalkReplyRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.service.S3Service;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final InfoTalkLoveRepository infoTalkLoveRepository;
    private final AddressRepository addressRepository;
    private final S3Service s3Service;

    // 정보토크 게시글 작성
    @Transactional
    public InfoResponseDTO.InfoTalkSaveDTO saveInfoTalk(InfoRequestDTO.InfoTalkDTO dto, Member member) {


        InfoTalk infoTalk = InfoTalk.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .save(Save.저장)
                .member(member)
                .build();

        infoTalkRepository.save(infoTalk);

        // 해시태그 리스트
        List<String> infoTags = new ArrayList<>();
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tag : dto.getTags()) {
                infoTags.add(tag);
                InfoHashTag infoHashTag = InfoHashTag.builder()
                        .infoTalk(infoTalk)
                        .tag(tag)
                        .build();
                infoHashTagRepository.save(infoHashTag);
            }

        }

        InfoTalkSaveDTO result = InfoTalkSaveDTO.builder()
                .id(infoTalk.getId())
                .nickname(member.getNickname())
                .title(infoTalk.getTitle())
                .content(infoTalk.getContent())
                .tag(infoTags)
                .build();

        return result;
    }

    @Transactional
    public String uploadImages(Long id, List<MultipartFile> multipartFiles) {
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


        return id + "번 정보토크 사진 저장완료";
    }


    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new IllegalArgumentException("사진 입력 오류입니다.");
        }
    }

    @Transactional
    public String deleteInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        if (member != infoTalk.getMember()) {
            throw new IllegalArgumentException("작성자가 아니라서 삭제할 수 없습니다");
        }

        for (InfoPicture infoPicture : infoTalk.getInfoPictures()) {
            s3Service.fileDelete(infoPicture.getUrl());
        }

        infoTalkRepository.delete(infoTalk);

        return id + " 번 게시글 삭제완료";
    }

    @Transactional
    public ResponseEntity<?> updateInfoTalk(InfoRequestDTO.InfoTalkDTO dto, Long id) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        infoTalk.update(dto.getTitle(), dto.getContent());

        return ResponseEntity.ok(id + " 번 게시글 수정완료");
    }

    @Transactional
    // 정보토크 게시글 1개 조회
    public InfoResponseDTO.InfoTalkViewDTO getInfoTalk(Long id, Member member) {

        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        if (infoTalkLoveRepository.findByInfoTalkAndMember(infoTalk, member) == null) {
            infoTalk.setLove(false);
        } else {
            infoTalk.setLove(true);
        }

        infoTalk.plusView(infoTalk.getView() + 1);

        // 해시태그 리스트
        List<String> infoTags = new ArrayList<>();
        if (!infoTalk.getInfoHashTags().isEmpty()) {
            for (InfoHashTag infoHashTag : infoTalk.getInfoHashTags()) {
                infoTags.add(infoHashTag.getTag());
            }
        }

        // 정보토크 사진 리스트
        List<String> infoImages = new ArrayList<>();
        if (!infoTalk.getInfoPictures().isEmpty()) {
            for (InfoPicture infoPicture : infoTalk.getInfoPictures()) {
                infoImages.add(infoPicture.getUrl());
            }
        }

        // 정보토크 댓글, 대댓글 DTO 생성
        List<InfoResponseDTO.InfoTalkCommentViewDTO> infoTalkCommentViewDTOList = new ArrayList<>();
        if (infoTalk.getInfoTalkComments().isEmpty()) {
        } else {
            for (InfoTalkComment infoTalkComment : infoTalk.getInfoTalkComments()) {
                List<InfoResponseDTO.InfoTalkReplyViewDTO> infoTalkReplyViewDTOList = new ArrayList<>();
                if (infoTalkComment.getReplyList().isEmpty()) {
                } else {
                    for (InfoTalkReply infoTalkReply : infoTalkComment.getReplyList()) {
                        InfoTalkReplyViewDTO infoTalkReplyViewDTO = InfoTalkReplyViewDTO.builder()
                                .createdAt(infoTalkReply.getCreatedAt())
                                .updatedAt(infoTalkReply.getUpdatedAt())
                                .replyId(infoTalkReply.getId())
                                .replyNickName(infoTalkReply.getMember().getNickname())
                                .content(infoTalkReply.getContent())
                                .build();
                        infoTalkReplyViewDTOList.add(infoTalkReplyViewDTO);
                    }
                }
                InfoTalkCommentViewDTO infoTalkCommentViewDTO = InfoTalkCommentViewDTO.builder()
                        .createdAt(infoTalkComment.getCreatedAt())
                        .updatedAt(infoTalkComment.getUpdatedAt())
                        .commentId(infoTalkComment.getId())
                        .commentNickName(infoTalkComment.getMember().getNickname())
                        .content(infoTalkComment.getContent())
                        .infoTalkReplies(infoTalkReplyViewDTOList)
                        .build();
                infoTalkCommentViewDTOList.add(infoTalkCommentViewDTO);
            }
        }

        InfoTalkViewDTO result = InfoTalkViewDTO.builder()
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
                .infoPictureImages(infoImages)
                .infoTalkComments(infoTalkCommentViewDTOList)
                .build();

        return result;
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
    public String saveComment(InfoRequestDTO.CommentDTO dto, Member member) {

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

        return dto.getId() + "번 게시글의 댓글을 작성완료하였습니다.";
    }

    @Transactional
    public String deleteComment(Long commentId, Member member) {
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

        return commentId + "번 댓글 삭제 완료";
    }

    @Transactional
    public ResponseEntity<?> saveReply(InfoRequestDTO.CommentDTO dto, Member member) {
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

    @Transactional
    public ResponseEntity<?> saveLove(Long id, Member member) {
        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        if (infoTalk.getSetLove()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 글입니다");
        }

        InfoTalkLove infoTalkLove = InfoTalkLove.builder()
                .infoTalk(infoTalk)
                .member(member)
                .build();

        infoTalk.plusLove(infoTalk.getLove() + 1);
        infoTalk.setLove(true);

        infoTalkLoveRepository.save(infoTalkLove);

        return ResponseEntity.ok(id + " 글에 대해 좋아요를 눌렀습니다.");
    }

    @Transactional
    public ResponseEntity<?> deleteLove(Long id, Member member) {


        InfoTalk infoTalk = infoTalkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 게시글을 찾을 수 없습니다."));

        InfoTalkLove infoTalkLove = infoTalkLoveRepository.findByInfoTalkAndMember(infoTalk, member);

        infoTalk.setLove(false);
        infoTalk.plusLove(infoTalk.getLove() - 1);

        infoTalkLoveRepository.delete(infoTalkLove);

        return ResponseEntity.ok(id + " 글에 대해 좋아요를 취소했습니다.");
    }
}
