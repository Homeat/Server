package homeat.backend.domain.user.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.QMember;
import homeat.backend.domain.user.entity.QMemberInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

    @Override
    public Optional<List<Member>> findMemberByCriteria(Integer ageIndex, Gender gender, Long income) {
        QMemberInfo qMemberInfo = QMemberInfo.memberInfo;
        QMember qMember = QMember.member;

        Integer[] ageRange = new Integer[2];
        ageRange[0] = ageIndex * 10;
        ageRange[1] = ageIndex * 10 + 9;

        Integer currentYear = LocalDate.now().getYear();
        Integer[] birthRange = new Integer[2];
        birthRange[0] = currentYear - ageRange[1] + 1;
        birthRange[1] = currentYear - ageRange[0] + 1;

        List<Member> members =
                queryFactory.selectFrom(qMember)
                        .innerJoin(qMemberInfo).on(qMemberInfo.member.eq(qMember))
                .where(qMemberInfo.birth.year()
                        .between(birthRange[0], birthRange[1])
                        .and(qMemberInfo.gender.eq(gender))
                        .and(qMemberInfo.income.divide(100).eq(income/100)))
                .fetch();

        return Optional.ofNullable(members);
    }
}
