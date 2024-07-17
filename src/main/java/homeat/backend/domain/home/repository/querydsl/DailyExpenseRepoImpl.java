package homeat.backend.domain.home.repository.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.entity.QDailyExpense;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class DailyExpenseRepoImpl implements DailyExpenseRepoCST{
    private final JPAQueryFactory queryFactory;

    public DailyExpenseRepoImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 주간 지출 금액 조회
     */
    @Override
    public long sumPricesBetweenDates(LocalDate startDate, LocalDate endDate, FinanceData financeData) {

        QDailyExpense dailyExpense = QDailyExpense.dailyExpense;

        Long sum = queryFactory
                .select(dailyExpense.todayJipbapPrice.add(dailyExpense.todayOutPrice).sum())
                .from(dailyExpense)
                .where(dailyExpense.date.goe(startDate)
                        .and(dailyExpense.date.lt(endDate.plusDays(1)))
                        .and(dailyExpense.financeData.id.eq(financeData.getId())))
                .fetchOne();

        return sum != null ? sum : 0L;
    }

    /**
     * 하루 지출 엔티티 존재 유무 조회
     */
    @Override
    public Optional<DailyExpense> findDailyExpenseByFinanceDataIdAndDate(Long financeDataId, LocalDate date) {

        QDailyExpense dailyExpense = QDailyExpense.dailyExpense;

        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", dailyExpense.date);
        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return Optional.ofNullable(queryFactory
                .selectFrom(dailyExpense)
                .where(dailyExpense.financeData.id.eq(financeDataId)
                        .and(formattedDate.eq(dateString)))
                .fetchOne());
    }

}
