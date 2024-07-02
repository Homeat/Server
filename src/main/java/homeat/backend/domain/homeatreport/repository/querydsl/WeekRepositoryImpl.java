package homeat.backend.domain.homeatreport.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.QFinanceData;
import homeat.backend.domain.homeatreport.entity.QWeekAnalyze;
import homeat.backend.domain.homeatreport.entity.QWeekCheck;
import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import homeat.backend.domain.user.entity.QMember;
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

    /**
     * memberId와 weekIdx를 이용하여 해당 멤버에 대한 weekIdx주차
     * @param memberId
     * @param weekIdx
     * @return
     */
    @Override
    public Optional<WeekAnalyze> findWeekAnalyzeByMemberIdAndWeekIdxAndInputDate(Long memberId, Integer weekIdx, Integer input_year, Integer input_month) {
        QWeekAnalyze qWeekAnalyze = QWeekAnalyze.weekAnalyze;
        QFinanceData qFinanceData = QFinanceData.financeData;
        QMember qMember = QMember.member;
        return Optional.ofNullable(queryFactory.selectFrom(qWeekAnalyze)
                .leftJoin(qWeekAnalyze.financeData, qFinanceData)
                .leftJoin(qFinanceData.member, qMember)
                .where(qMember.id.eq(memberId)
                        .and(qWeekAnalyze.weekIdx.eq(weekIdx))
                        .and(qWeekAnalyze.createdAt.year().eq(input_year))
                        .and(qWeekAnalyze.createdAt.month().eq(input_month))
                )
                .fetchOne());
    }

    @Override
    public Optional<WeekCheck> findWeekByMemberIdOrderByWeekCheckIdDesc(Long member_id) {
        QWeekCheck qWeekCheck = QWeekCheck.weekCheck;
        QFinanceData qFinanceData = QFinanceData.financeData;
        QMember qMember = QMember.member;
        return Optional.ofNullable(queryFactory.selectFrom(qWeekCheck)
                .leftJoin(qWeekCheck.financeData, qFinanceData)
                        .leftJoin(qFinanceData.member, qMember)
                .where(qMember.id.eq(member_id))
                        .orderBy(qWeekCheck.id.desc())
                .fetchFirst()); // 정렬된 순서에서 첫번째만 조회
    }

    private Slice<WeekCheck> checkEndPageWeek(Pageable pageable, List<WeekCheck> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<WeekCheck> findWeekByMemberIdAsc(Long member_id, Long lastWeekCheckId, Pageable pageable) {

        QWeekCheck qWeekCheck = QWeekCheck.weekCheck;
        QFinanceData qFinanceData = QFinanceData.financeData;
        QMember qMember = QMember.member;

        List<WeekCheck> weekChecks = queryFactory.selectFrom(qWeekCheck)
                .leftJoin(qWeekCheck.financeData, qFinanceData)
                .leftJoin(qFinanceData.member, qMember)
                .where(qMember.id.eq(member_id)
                        .and(qWeekCheck.personalWeekCheckNum.gt(lastWeekCheckId)))
                .orderBy(qWeekCheck.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkEndPageWeek(pageable, weekChecks);
    }

    /**
     * 기존의 lastWeekCheckId는 다른 memberId에 의해 영향을 받기 때문에 멤버 개인 별 weekCheck의 개수를 구하여 개별적으로 접근
     * @param memberId
     * @return
     */
    @Override
    public Long countPersonalWeekChecks(Long memberId) {

        QWeekCheck qWeekCheck = QWeekCheck.weekCheck;
        QFinanceData qFinanceData = QFinanceData.financeData;
        QMember qMember = QMember.member;

        return queryFactory.select(qWeekCheck.count())
                .from(qWeekCheck)
                .leftJoin(qWeekCheck.financeData, qFinanceData)
                .leftJoin(qFinanceData.member, qMember)
                .where(qMember.id.eq(memberId))
                .fetchOne();
    }


//    /**
//     * memberId를 통해 1번째 Week Check 엔티티부터 오름차순으로 모두 가져와서 리스트로 반환
//     * @param memberId
//     * @return
//     */
//    @Override
//    public List<WeekCheck> findAllByMemberIdOrderByWeekCheckIdAsc(Long memberId) {
//        QWeekCheck qWeekCheck = QWeekCheck.weekCheck;
//        QFinanceData qFinanceData = QFinanceData.financeData;
//        QMember qMember = QMember.member;
//        return queryFactory
//                .selectFrom(qWeekCheck)
//                .leftJoin(qWeekCheck.financeData, qFinanceData)
//                .leftJoin(qFinanceData.member, qMember)
//                .where(qMember.id.eq(memberId))
//                .orderBy(qWeekCheck.id.asc())
//                .fetch();
//    }
//
//    /**
//     * memberId를 사용하여 특정 멤버의 모든 WeekAnalyze 엔티티를 최신순으로 정렬한 뒤 맨 위(최신)를 조회
//     * @param memberId
//     * @return
//     */
//    @Override
//    public Optional<WeekAnalyze> findWeekAnalyzeTopByMemberOrderByIdDesc(Long memberId) {
//        QWeekAnalyze qWeekAnalyze = QWeekAnalyze.weekAnalyze;
//        QFinanceData qFinanceData = QFinanceData.financeData;
//        QMember qMember = QMember.member;
//
//        return Optional.ofNullable(queryFactory.selectFrom(qWeekAnalyze)
//                .leftJoin(qWeekAnalyze.financeData, qFinanceData)
//                .leftJoin(qFinanceData.member, qMember)
//                .where(qMember.id.eq(memberId))
//                .orderBy(qWeekAnalyze.id.desc())
//                .fetchFirst()
//        );
//    }
//
//    /**
//     * memberId를 사용하여 특정 멤버의 모든 WeekCheck 엔티티를 최신순으로 정렬한 뒤 맨 위(최신)를 조회
//     * @param memberId
//     * @return
//     */
//    @Override
//    public Optional<WeekCheck> findWeekCheckTopByMemberIdOrderByIdDesc(Long memberId) {
//        QWeekCheck qWeekCheck = QWeekCheck.weekCheck;
//        QFinanceData qFinanceData = QFinanceData.financeData;
//        QMember qMember = QMember.member;
//
//        return Optional.ofNullable(queryFactory.selectFrom(qWeekCheck)
//                .leftJoin(qWeekCheck.financeData, qFinanceData)
//                .leftJoin(qFinanceData.member, qMember)
//                .where(qMember.id.eq(memberId))
//                .orderBy(qWeekCheck.id.desc())
//                .fetchFirst()
//        );
//    }



}
