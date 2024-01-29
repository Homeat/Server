package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QInfoPicture.infoPicture;
import static homeat.backend.domain.post.entity.QInfoTalk.infoTalk;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.QInfoPicture;
import homeat.backend.domain.post.entity.QInfoTalk;
import javax.persistence.EntityManager;

public class InfoTalkRepositoryImpl implements InfoTalkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public InfoTalkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public InfoTalk findByInfoTalkId(Long id) {
        return queryFactory
                .selectFrom(infoTalk)
                .join(infoTalk.infoPictures, infoPicture).fetchJoin()
                .where(infoTalk.id.eq(id))
                .fetchOne();
    }
}
