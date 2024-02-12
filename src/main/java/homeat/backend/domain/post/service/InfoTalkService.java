package homeat.backend.domain.post.service;

import homeat.backend.domain.post.dto.InfoHashTagDTO;
import homeat.backend.domain.post.dto.InfoTalkDTO;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodPicture;
import homeat.backend.domain.post.entity.InfoHashTag;
import homeat.backend.domain.post.entity.InfoPicture;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.Save;
import homeat.backend.domain.post.repository.InfoHashTagRepository;
import homeat.backend.domain.post.repository.InfoPictureRepository;
import homeat.backend.domain.post.repository.InfoTalkRepository;
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

    public ResponseEntity<?> tempSaveInfoTalk(InfoTalkDTO dto) {
        InfoTalk infoTalk = InfoTalk.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .save(Save.임시저장)
                .build();

        infoTalkRepository.save(infoTalk);


        return ResponseEntity.ok("정보토크 임시저장완료");
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


}
