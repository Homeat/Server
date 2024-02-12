package homeat.backend.domain.home.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.entity.QDailyExpense;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DailyExpenseRepoImpl implements DailyExpenseRepoCST {

    private final JPAQueryFactory  queryFactory;

    public DailyExpenseRepoImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }


    /**
     * member_id와 week 시작일/마지막일을 통해 dailyexpense 찾기
     * @param member_id
     * @param startOfWeek
     * @param endOfWeek
     * @return
     */
    @Override
    public List<DailyExpense> findDailyExpenseByMemberIdAndDateBetween(Long member_id, LocalDate startOfWeek, LocalDate endOfWeek) {
        QDailyExpense qDailyExpense = QDailyExpense.dailyExpense;
        return queryFactory.selectFrom(qDailyExpense)
                .where(qDailyExpense.member.id.eq(member_id)
                        .and(qDailyExpense.createdAt.between(startOfWeek.atStartOfDay(), endOfWeek.atStartOfDay())))
                .fetch();
    }
}
