package homeat.backend.domain.analyze.repository.querydsl;

import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.user.entity.Member;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FinanceDataRepositoryImpl implements FinanceDataRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FinanceDataRepositoryImpl(EntityManager em) {
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

    @Override
    public Optional<FinanceData> findByMemberAndYearAndMonth(Member member, String year, String month) {
        QFinanceData financeData = QFinanceData.financeData;

        // 문자열로 된 년과 월을 합쳐서 'YYYY-MM' 형태의 문자열을 만듭니다.
        String yearMonthString = year + "-" + month;

        // 'createdAt' 필드의 년과 월을 'YYYY-MM' 형태의 문자열로 변환합니다.
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m')", financeData.createdAt);

        FinanceData foundFinanceData = queryFactory
                .selectFrom(financeData)
                .where(
                        financeData.member.eq(member),
                        formattedDate.eq(yearMonthString)
                )
                .fetchOne();

        return Optional.ofNullable(foundFinanceData);
    }
}
