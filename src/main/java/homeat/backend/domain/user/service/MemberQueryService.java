package homeat.backend.domain.user.service;

import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;

    @Transactional
    public Member mypageMember(Long id) {
        return memberRepository.findById(id).orElseThrow();
    }

    @Transactional
    public MemberInfo mypageMemberInfo(Long memberId) {
        return memberInfoRepository.findMemberInfoByMemberId(memberId);
    }
}
