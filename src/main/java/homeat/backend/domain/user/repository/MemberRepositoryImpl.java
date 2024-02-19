package homeat.backend.domain.user.repository;

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

import static homeat.backend.domain.user.entity.QMember.member;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

    @Override
    public Optional<List<Member>> findMemberByCriteria(Integer[] ageRange, Gender gender, Long income) {
        QMemberInfo qMemberInfo = QMemberInfo.memberInfo;
        QMember qMember = QMember.member;

        Integer startYear = ageRange[0];
        Integer endYear = ageRange[1];

        List<Member> members =
                queryFactory.selectFrom(qMember)
                        .innerJoin(qMemberInfo).on(qMemberInfo.member.eq(qMember))
                .where(qMemberInfo.birth.year()
                        .between(startYear, endYear)
                        .and(qMemberInfo.gender.eq(gender))
                        .and(qMemberInfo.income.divide(100).eq(income/100)))
                .fetch();

        return Optional.ofNullable(members);
    }
}
