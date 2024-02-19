package homeat.backend.domain.user.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.controller.MemberConverter;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.handler.MemberErrorStatus;
import homeat.backend.domain.user.handler.MemberHandler;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekRepository weekRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final S3Service s3Service;

    @Transactional
    public Member joinMember(MemberRequest.JoinDto request) {

        // 중복 이메일, 닉네임 -> dto 에서 처리
        request.setPassword(encoder.encode(request.getPassword()));
        Member newMember = MemberConverter.toMember(request);

        return memberRepository.save(newMember);
    }

    @Transactional
    public String loginMember(MemberRequest.LoginDto request) {
        // 이메일 존재 여부
        Member selectedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    throw new MemberHandler(MemberErrorStatus.EMAIL_NOT_FOUND);
                });

        // 비밀번호 일치 여부
        if (!encoder.matches(request.getPassword(), selectedMember.getPassword())) {
            throw new MemberHandler(MemberErrorStatus.INVALID_PASSWORD);
        }

        return jwtUtil.createJwt(selectedMember.getId());
    }

    @Transactional
    public String loginMember(Long memberId) {
        return jwtUtil.createJwt(memberId);
    }

    public LocalDateTime getJwtExpiredAt(String token) {
        Date expiredAt = jwtUtil.getExpiredAt(token);
        return LocalDateTime.ofInstant(expiredAt.toInstant(), ZoneId.systemDefault());
    }

    @Transactional
    public MemberInfo saveMemberInfo(MemberRequest.CreateInfoDto request, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND);
                });
        MemberInfo newMemberInfo = MemberConverter.toMemberInfo(request, selectedMember);

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
    public String sendCodeToEmail(MemberRequest.EmailVerifyDto request) {
        String authCode;

        try {
            authCode = mailService.createCode();
            String title = "홈잇 이메일 인증번호";
            String content = String.format("홈잇 이메일 인증번호 입니다.\n%s", authCode);
            mailService.sendEmail(request.getEmail(), title, content);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new MemberHandler(MemberErrorStatus.MAIL_BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new MemberHandler(MemberErrorStatus.AUTH_CODE_ERROR);
        }

        return authCode;
    }

    @Transactional
    public void updatePassword(MemberRequest.UpdatePasswordDto request, Long memberId) {
        Member selectedMember = memberRepository.findById(memberId).orElseThrow();

        if (!encoder.matches(request.getOriginPassword(), selectedMember.getPassword())) {
            throw new MemberHandler(MemberErrorStatus.INVALID_PASSWORD);
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
        if (request.getAddressId() != null) selectedMemberInfo.updateAddress(request.getAddressId());
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
