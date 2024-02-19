package homeat.backend.domain.user.repository;

import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<List<Member>> findMemberByCriteria(Integer[] ageRange, Gender gender, Long income);
}
