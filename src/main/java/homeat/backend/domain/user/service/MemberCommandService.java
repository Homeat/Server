package homeat.backend.domain.user.service;

import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.address.repository.AddressRepository;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.controller.MemberErrorStatus;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.security.jwt.JwtUtil;
import homeat.backend.global.service.MailService;
import homeat.backend.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekRepository weekRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final S3Service s3Service;

    @Transactional
    public Member joinMember(MemberRequest.JoinDto request) {

        // 중복 이메일, 닉네임 -> dto 에서 처리
        request.setPassword(encoder.encode(request.getPassword()));
        Member newMember = MemberMapper.toMember(request);

        return memberRepository.save(newMember);
    }

    @Transactional
    public MemberInfo saveMemberInfo(MemberRequest.CreateInfoDto request, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        Address selectedAddress = addressRepository.findById(request.getAdderessId())
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.ADDRESS_NOT_FOUND));
        MemberInfo newMemberInfo = MemberMapper.toMemberInfo(request, selectedMember, selectedAddress);

        FinanceData newFinanceData = FinanceData.builder()
                .member(selectedMember)
                .build();
        financeDataRepository.save(newFinanceData);

        Week newWeek = Week.builder()
                .financeData(newFinanceData)
                .goal_price(request.getGoalPrice())
                .next_goal_price(request.getGoalPrice())
                .build();
        weekRepository.save(newWeek);

        return memberInfoRepository.save(newMemberInfo);
    }

    @Transactional
    public void updatePassword(MemberRequest.UpdatePasswordDto request, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();

        if (!encoder.matches(request.getOriginPassword(), selectedMember.getPassword())) {
            throw new GeneralException(MemberErrorStatus.INVALID_PASSWORD);
        }

        selectedMember.updatePassword(encoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void updateInfo(MemberRequest.UpdateInfoDto request, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();
        MemberInfo selectedMemberInfo = memberInfoRepository.findMemberInfoByMemberId(memberId);

        if (request.getEmail() != null) selectedMember.updateEmail(request.getEmail());
        if (request.getNickname() != null) selectedMember.updateNickname(request.getNickname());
        if (request.getIncome() != null) selectedMemberInfo.updateIncome(request.getIncome());
        if (request.getAddressId() != null) {
            Address selectedAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new GeneralException(MemberErrorStatus.ADDRESS_NOT_FOUND));
            selectedMemberInfo.updateAddress(selectedAddress);
        }
    }

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
