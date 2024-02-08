package homeat.backend.domain.home.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.entity.QDailyExpense;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;

public class DailyExpenseRepoImpl implements DailyExpenseRepoCST{
    private final JPAQueryFactory queryFactory;

    public DailyExpenseRepoImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 주간 지출 금액 조회
     */
    @Override
    public Long sumPricesBetweenDates(LocalDate startDate, LocalDate endDate) {

        QDailyExpense dailyExpense = QDailyExpense.dailyExpense;

        return queryFactory
                .select(dailyExpense.todayJipbapPrice.add(dailyExpense.todayOutPrice).sum())
                .from(dailyExpense)
                .where(dailyExpense.createdAt.year().goe(startDate.getYear())
                        .and(dailyExpense.createdAt.month().goe(startDate.getMonthValue())
                                .and(dailyExpense.createdAt.dayOfMonth().goe(startDate.getDayOfMonth())))
                        .and(dailyExpense.createdAt.year().loe(endDate.getYear())
                                .and(dailyExpense.createdAt.month().loe(endDate.getMonthValue())
                                        .and(dailyExpense.createdAt.dayOfMonth().loe(endDate.getDayOfMonth())))))
                .fetchOne();
    }

    /**
     * 하루 지출 엔티티 존재 유무 조회
     */
    @Override
    public Optional<DailyExpense> findDailyExpenseByFinanceDataIdAndDate(Long financeDataId, LocalDate date) {

        QDailyExpense dailyExpense = QDailyExpense.dailyExpense;

        return Optional.ofNullable(queryFactory
                .selectFrom(dailyExpense)
                .where(dailyExpense.financeData.id.eq(financeDataId)
                        .and(dailyExpense.createdAt.year().eq(date.getYear())
                                .and(dailyExpense.createdAt.month().eq(date.getMonthValue())
                                        .and(dailyExpense.createdAt.dayOfMonth().eq(date.getDayOfMonth())))))
                .fetchOne());
    }
}
