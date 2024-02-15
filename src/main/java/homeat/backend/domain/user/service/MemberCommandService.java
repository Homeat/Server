package homeat.backend.domain.user.service;

import homeat.backend.domain.user.controller.MemberConverter;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.handler.MemberErrorStatus;
import homeat.backend.domain.user.handler.MemberHandler;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

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

        return memberInfoRepository.save(newMemberInfo);
    }
}
