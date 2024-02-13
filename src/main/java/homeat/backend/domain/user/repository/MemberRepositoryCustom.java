package homeat.backend.domain.user.repository;

import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberByCriteria(Integer[] ageRange, Gender gender, Long income);
}
