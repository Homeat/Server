package homeat.backend.domain.analyze.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.user.entity.Member;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;

public class FinanceRepositoryImpl implements FinanceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FinanceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * '회원가입 시 row 추가 + 매달 1일 row 추가' 에 대한 예외처리
     */
    @Override
    public Optional<FinanceData> findByMemberAndDate(Member member, LocalDate date) {
        QFinanceData financeData = QFinanceData.financeData;

        FinanceData foundFinanceData = queryFactory.selectFrom(financeData)
                .where(financeData.member.eq(member)
                    .and(financeData.createdAt.year().eq(date.getYear())
                        .and(financeData.createdAt.month().eq(date.getMonthValue())
                                .and(financeData.createdAt.dayOfMonth().eq(date.getDayOfMonth())))))
                .fetchOne();


        return Optional.ofNullable(foundFinanceData);
    }

    /**
     * 해당 멤버의 가장 최근 finance(월) 객체 조회
     */
    @Override
    public Optional<FinanceData> findLatestFinanceDataIdByMember(Member member) {
        QFinanceData financeData = QFinanceData.financeData;

        FinanceData latestFinanceData = queryFactory.selectFrom(financeData)
                .where(financeData.member.eq(member))
                .orderBy(financeData.createdAt.desc())
                .fetchFirst();

        return Optional.ofNullable(latestFinanceData);
    }
}
