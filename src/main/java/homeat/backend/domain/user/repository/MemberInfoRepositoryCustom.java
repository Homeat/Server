package homeat.backend.domain.user.repository;


import homeat.backend.domain.user.entity.MemberInfo;

import java.util.Optional;

public interface MemberInfoRepositoryCustom {
    Optional<MemberInfo> findMemberInfoByMemberIdOptional(Long member_id);
}
