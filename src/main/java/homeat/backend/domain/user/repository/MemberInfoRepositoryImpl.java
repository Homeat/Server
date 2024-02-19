package homeat.backend.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.entity.QMemberInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static homeat.backend.domain.user.entity.QMemberInfo.memberInfo;

@Repository
public class MemberInfoRepositoryImpl implements MemberInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberInfoRepositoryImpl(EntityManager em) { this.queryFactory = new JPAQueryFactory(em); }

//    @Override
//    public MemberInfo findMemberInfoByMemberId(Long member_id) {
//        QMemberInfo qMemberInfo = memberInfo;
//        // QueryDSL을 사용하여 멤버 정보 조회
//        return queryFactory.selectFrom(memberInfo)
//                .where(memberInfo.member.id.eq(member_id))
//                .fetchOne();
//    }

}
