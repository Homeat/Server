package homeat.backend.domain.user.service;

import homeat.backend.domain.user.controller.MemberErrorStatus;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.security.LoginService;
import homeat.backend.global.security.jwt.JwtUtil;
import homeat.backend.global.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final LoginService loginService;
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public void insertMemberByEmail(HttpServletResponse response, MemberRequest.joinEmailDto requestDto) {
        Member newMember = MemberMapper.toEmailMember(requestDto.getEmail(), encoder.encode(requestDto.getPassword()));
        Member savedMember = memberRepository.save(newMember);

        issueToken(response, savedMember.getId());
    }

//    @Transactional
//    public void insertMemberByKakao(HttpServletRequest request, HttpServletResponse response, MemberRequest.joinKakaoDto requestDto) {
//        Member newMember = MemberMapper.toKakaoMember(requestDto.getKakaoId().toString());
//        Member savedMember = memberRepository.save(newMember);
//
//        issueToken(response, savedMember.getId());
//    }

    @Transactional
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = loginService.validateRefreshToken(request.getCookies());

        Long userId = jwtUtil.getUserId(refreshToken);
        String newAccessToken = loginService.issueAccessToken(userId);
        Cookie newRefreshToken = loginService.reissueRefreshToken(userId, refreshToken);

        response.addHeader("Authorization", newAccessToken);
        response.addCookie(newRefreshToken);
    }

    @Transactional
    public void findPassword(MemberRequest.findPasswordDto request) {
        Member selectedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.EMAIL_NOT_FOUND));

        selectedMember.updatePassword(encoder.encode(request.getNewPassword()));
    }

    public String certifyEmail(MemberRequest.emailCheckDto request) {
        if (memberRepository.existsByEmail(request.getEmail()))
            throw new GeneralException(MemberErrorStatus.EXIST_EMAIL);
        return sendCodeToEmail(request.getEmail());
    }

    public String verifyEmail(MemberRequest.emailCheckDto request) {
        if (!memberRepository.existsByEmail(request.getEmail()))
            throw new GeneralException(MemberErrorStatus.EMAIL_NOT_FOUND);
        return sendCodeToEmail(request.getEmail());
    }

    private void issueToken(HttpServletResponse response, Long userId) {
        String newAccessToken = loginService.issueAccessToken(userId);
        Cookie newRefreshToken = loginService.issueRefreshToken(userId);

        response.addHeader("Authorization", newAccessToken);
        response.addCookie(newRefreshToken);
    }

    private String sendCodeToEmail(String email) {
        try {
            String authCode = mailService.createCode();
            String title = "홈잇 이메일 인증번호";
            String content = String.format("홈잇 이메일 인증번호 입니다.%n%s", authCode);
            mailService.sendEmail(email, title, content);
            return authCode;
        } catch (MessagingException e) {
            throw new GeneralException(MemberErrorStatus.MAIL_BAD_REQUEST);
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralException(MemberErrorStatus.AUTH_CODE_ERROR);
        }
    }
}
