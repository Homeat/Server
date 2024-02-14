package homeat.backend.domain.homeatreport.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.homeatreport.entity.QWeek;
import homeat.backend.domain.homeatreport.entity.Week;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class WeekRepositoryImpl implements WeekRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WeekRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

    @Override
    public Optional<Week> findWeekByMemberIdAndFinanceDataId(Long member_id) {
        QWeek qWeek = QWeek.week;
        QFinanceData qFinanceData = QFinanceData.financeData;
        return Optional.ofNullable(queryFactory.selectFrom(qWeek)
                .leftJoin(qWeek.financeData, qFinanceData)
                .where(qFinanceData.member.id.eq(member_id)
                        .and(qWeek.financeData.id.eq(qFinanceData.id)))
                .fetchOne());

    }

    @Override
    public Slice<Week> findWeekByMemberIdAsc(Long member_id, Long lastWeekId, Pageable pageable) {
        QWeek qWeek = QWeek.week;
        QFinanceData qFinanceData = QFinanceData.financeData;
        List<Week> resultWeeks = queryFactory.selectFrom(qWeek)
                .leftJoin(qWeek.financeData, qFinanceData)
                .where(
                        qFinanceData.member.id.eq(member_id)
                                .and(qWeek.financeData.id.eq(qFinanceData.id))
                                .and(qWeek.id.lt(lastWeekId))
                )
                .orderBy(qWeek.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkEndPageWeek(pageable, resultWeeks);
    }

    private Slice<Week> checkEndPageWeek(Pageable pageable, List<Week> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

}
