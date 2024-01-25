package homeat.backend.domain.user.service;

import homeat.backend.domain.user.controller.MemberConverter;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.handler.MemberErrorStatus;
import homeat.backend.domain.user.handler.MemberHandler;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public Member joinMember(MemberRequest.JoinDto request) {

        // 중복 이메일 처리
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new MemberHandler(MemberErrorStatus.EXIST_EMAIL);
                });

        // 중복 닉네임 처리
        memberRepository.findByNickname(request.getNickname())
                .ifPresent(member -> {
                    throw new MemberHandler(MemberErrorStatus.EXIST_NICKNAME);
                });

        // 예외 없음
        request.setPassword(encoder.encode(request.getPassword()));
        Member newMember = MemberConverter.toMember(request);

        return memberRepository.save(newMember);
    }
}
