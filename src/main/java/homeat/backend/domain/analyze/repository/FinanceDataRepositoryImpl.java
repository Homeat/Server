package homeat.backend.domain.analyze.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.entity.QFinanceData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class FinanceDataRepositoryImpl implements FinanceDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public FinanceDataRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public FinanceData findByMemberIdAndCreatedYearAndCreatedMonth(Long member_id, Integer year, Integer month) {
        QFinanceData qFinanceData = QFinanceData.financeData;
        return queryFactory.selectFrom(qFinanceData)
                .where(qFinanceData.member.id.eq(member_id)
                        .and(qFinanceData.createdAt.year().eq(year)
                                .and(qFinanceData.createdAt.month().eq(month))))
                .fetchOne();
    }
}
