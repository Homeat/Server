package homeat.backend.domain.user.service;

import homeat.backend.domain.user.controller.MemberConverter;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCommandService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(MemberRequest.CreateDto request) {

        Member newMember = MemberConverter.toMember(request);
        return memberRepository.save(newMember);
    }
}
