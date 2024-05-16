package homeat.backend.domain.user.repository;

import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.querydsl.MemberInfoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long>, MemberInfoRepositoryCustom {
    Optional<MemberInfo> findMemberInfoByMember(Member member);
}
