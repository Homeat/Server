package homeat.backend.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.QMemberInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static homeat.backend.domain.user.entity.QMember.member;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

    @Override
    public List<Member> findMemberByCriteria(Integer[] ageRange, Gender gender, Long income) {
        QMemberInfo qMemberInfo = QMemberInfo.memberInfo;

        LocalDate startDate = LocalDate.now().minusYears(ageRange[1]);
        LocalDate endDate = LocalDate.now().minusYears(ageRange[0]);

        List<Member> members = queryFactory.selectFrom(member)
                .innerJoin(qMemberInfo.member, member)
                .where(qMemberInfo.birth.between(startDate, endDate)
                        .and(qMemberInfo.gender.eq(gender))
                        .and(qMemberInfo.income.divide(100).eq(income/100)))
                .fetch();

        return members;
    }
}
