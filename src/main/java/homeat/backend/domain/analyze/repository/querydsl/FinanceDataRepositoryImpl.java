package homeat.backend.domain.analyze.repository.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.user.entity.Member;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public Optional<FinanceData> findByMemberAndCreatedAt(Member member, LocalDate date) {
        QFinanceData financeData = QFinanceData.financeData;

//        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", financeData.createdAt);
//        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//        FinanceData foundFinanceData = queryFactory.selectFrom(financeData)
//                .where(financeData.member.eq(member)
//                        .and(formattedDate.eq(dateString)))
//                .fetchOne();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        FinanceData foundFinanceData = queryFactory.selectFrom(financeData)
                .where(financeData.member.eq(member)
                        .and(financeData.createdAt.between(startOfDay, endOfDay)))
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

    /**
     * 요청 연, 월에 대한 finance id 조회
     */
    @Override
    public Optional<FinanceData> findByMemberAndYearAndMonth(Member member, int year, int month) {
        QFinanceData financeData = QFinanceData.financeData;

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        List<FinanceData> financeDataList = queryFactory.selectFrom(financeData)
                .where(
                        financeData.member.eq(member)
                                .and(financeData.createdAt.between(startOfMonth, endOfMonth))
                )
                .fetch();

        return financeDataList.stream().findAny();
    }

}
