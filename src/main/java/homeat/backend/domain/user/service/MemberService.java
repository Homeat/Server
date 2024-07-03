package homeat.backend.domain.user.service;

import homeat.backend.domain.user.controller.MemberErrorStatus;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.LoginType;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.security.LoginService;
import homeat.backend.global.security.jwt.JwtUtil;
import homeat.backend.global.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final WebClient webClient;

    @Value("${kakao.admin.key}")
    private String kakaoAdminKey;

    @Transactional
    public String insertMemberByEmail(HttpServletResponse response, MemberRequest.joinEmailDto requestDto) {
        Member newMember = MemberMapper.toEmailMember(requestDto.getEmail(), encoder.encode(requestDto.getPassword()));
        Member savedMember = memberRepository.save(newMember);

        return issueToken(savedMember.getId(), response);
    }

    @Transactional
    public String insertMemberByKakao(HttpServletResponse response, MemberRequest.joinKakaoDto requestDto) {
        validateKakaoUser(requestDto.getKakaoId(), requestDto.getNickname());

        if (memberRepository.existsByEmailAndLoginType(requestDto.getKakaoId().toString(), LoginType.KAKAO))
            throw new GeneralException(MemberErrorStatus.EXIST_KAKAO);

        Member newMember = MemberMapper.toKakaoMember(requestDto.getKakaoId().toString());
        memberRepository.save(newMember);

        return issueToken(newMember.getId(), response);
    }

    @Transactional
    public String loginMemberByKakao(HttpServletResponse response, MemberRequest.joinKakaoDto requestDto) {
        validateKakaoUser(requestDto.getKakaoId(), requestDto.getNickname());
        Member selectedMember = memberRepository.findByEmailAndLoginType(requestDto.getKakaoId().toString(), LoginType.KAKAO)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.KAKAO_NOT_FOUND));

        return issueToken(selectedMember.getId(), response);
    }

    @Transactional
    public String reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = loginService.validateRefreshToken(request.getCookies());

        Long userId = jwtUtil.getUserId(refreshToken);
        String newAccessToken = loginService.issueAccessToken(userId);
//        Cookie newRefreshToken = loginService.reissueRefreshToken(userId, refreshToken);
        String newRefreshToken = loginService.reissueRefreshToken(userId, refreshToken);

        response.addHeader("Authorization", newAccessToken);
        return newRefreshToken;
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

    private String issueToken(Long memberId, HttpServletResponse response) {
        String newAccessToken = loginService.issueAccessToken(memberId);
//        Cookie newRefreshToken = loginService.issueRefreshToken(memberId);
        String newRefreshToken = loginService.issueRefreshToken(memberId);

        response.addHeader("Authorization", newAccessToken);
//        response.addCookie(newRefreshToken);
        return newRefreshToken;
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

    private void validateKakaoUser(Long kakaoId, String nickname) {
        MemberResponse.joinKakaoDto kakaoUserMeResponse = webClient.get()
                .uri("/v2/user/me", uriBuilder -> uriBuilder
                        .queryParam("property_keys", "[\"kakao_account.profile\"]")
                        .queryParam("target_id_type", "user_id")
                        .queryParam("target_id", kakaoId.toString())
                        .build())
                .header("Authorization", "KakaoAK " + kakaoAdminKey)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new GeneralException(MemberErrorStatus.KAKAO_BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new GeneralException(MemberErrorStatus.KAKAO_SERVER_ERROR)))
                .bodyToMono(MemberResponse.joinKakaoDto.class)
                .block();

        System.out.println(nickname);
        System.out.println(kakaoUserMeResponse.getKakaoAccount().getProfile().getNickname());
        if (!nickname.equals(kakaoUserMeResponse.getKakaoAccount().getProfile().getNickname()))
            throw new GeneralException(MemberErrorStatus.KAKAO_NICKNAME_MISMATCH);
    }
}