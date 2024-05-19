package homeat.backend.domain.user.service;


import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Transactional
    public void updateProfileImg(MultipartFile multipartProfileImg, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();
        String newProfileImgUrl = s3Service.uploadProfileImg(multipartProfileImg);
        if (!selectedMember.getProfileImgUrl().equals("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png")) {
            s3Service.fileDelete(selectedMember.getProfileImgUrl());
        }
        selectedMember.updateProfileImgUrl(newProfileImgUrl);
    }

    @Transactional
    public void deleteProfileImg(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();
        if (!selectedMember.getProfileImgUrl().equals("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png")) {
            s3Service.fileDelete(selectedMember.getProfileImgUrl());
        }
        selectedMember.updateProfileImgUrl("https://homeat-dev-s3.s3.ap-northeast-2.amazonaws.com/homeat/default/default_icon.png");
    }

    @Transactional
    public void withdraw(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();
        selectedMember.withdraw();
    }

    @Transactional
    public void reactivate(Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();
        selectedMember.reactivate();
    }
}
