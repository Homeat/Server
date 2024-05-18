package homeat.backend.domain.homeatreport.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.homeatreport.entity.QWeek_Analyze;
import homeat.backend.domain.homeatreport.entity.QWeek_Check;
import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.QMember;
import org.springframework.data.domain.Page;
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

    private Slice<Week> checkEndPageWeek(Pageable pageable, List<Week> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Optional<Week_Analyze> findWeekAnalyzeByMemberIdAndWeekIdx(Long memberId, Integer weekIdx) {
        QWeek_Analyze qWeekAnalyze = QWeek_Analyze.week_Analyze;
        QFinanceData qFinanceData = QFinanceData.financeData;
        QMember qMember = QMember.member;
        return Optional.ofNullable(queryFactory.selectFrom(qWeekAnalyze)
                .leftJoin(qWeekAnalyze.financeData, qFinanceData)
                .leftJoin(qFinanceData.member, qMember)
                .where(qFinanceData.member.id.eq(memberId)
                        .and(qWeekAnalyze.financeData.id.eq(qFinanceData.id))
                        .and(qWeekAnalyze.weekIdx.eq(weekIdx)))
                .fetchOne());
    }

    @Override
    public Optional<Week_Check> findWeekByMemberIdOrderByWeekCheckIdDesc(Long member_id) {
        QWeek_Check qWeekCheck = QWeek_Check.week_Check;
        QFinanceData qFinanceData = QFinanceData.financeData;
        return Optional.ofNullable(queryFactory.selectFrom(qWeekCheck)
                .leftJoin(qWeekCheck.financeData, qFinanceData)
                .where(qFinanceData.member.id.eq(member_id)
                        .and(qWeekCheck.financeData.id.eq(qFinanceData.id)))
                        .orderBy(qWeekCheck.id.desc())
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

    @Override
    public List<Week_Check> findAllByMemberIdOrderByWeekCheckIdAsc(Long memberId) {
        QWeek_Check qWeekCheck = QWeek_Check.week_Check;
        QFinanceData qFinanceData = QFinanceData.financeData;
        return queryFactory
                .selectFrom(qWeekCheck)
                .leftJoin(qWeekCheck.financeData, qFinanceData)
                .where(qFinanceData.member.id.eq(memberId)
                        .and(qWeekCheck.financeData.id.eq(qFinanceData.id))
                )
                .orderBy(qWeekCheck.id.asc())
                .fetch();
    }

}
