package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QFoodTalk.foodTalk;
import static homeat.backend.domain.post.entity.QInfoHashTag.infoHashTag;
import static homeat.backend.domain.post.entity.QInfoPicture.infoPicture;
import static homeat.backend.domain.post.entity.QInfoTalk.infoTalk;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.QInfoHashTag;
import homeat.backend.domain.post.entity.QInfoPicture;
import homeat.backend.domain.post.entity.QInfoTalk;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class InfoTalkRepositoryImpl implements InfoTalkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public InfoTalkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private Slice<InfoTalk> checkEndPage(Pageable pageable, List<InfoTalk> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize()); //한개더 가져왔으니 더 가져온 데이터 삭제
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression search(String search) {
        return hasText(search) ? infoTalk.title.contains(search).or(infoTalk.content.contains(search).or(infoHashTag.tag.eq(search)))  : null;

    }

    @Override
    public InfoTalk findByInfoTalkId(Long id) {
        return queryFactory
                .selectFrom(infoTalk)
                .join(infoTalk.infoPictures, infoPicture).fetchJoin()
                .where(infoTalk.id.eq(id))
                .fetchOne();
    }

    @Override
    public Slice<InfoTalk> findByIdLessThanOrderByIdDesc(InfoTalkSearchCondition condition, Long lastInfoTalkId,
                                                         Pageable pageable) {
        List<InfoTalk> results = queryFactory
                .selectFrom(infoTalk)
                .where(
                        infoTalk.id.lt(lastInfoTalkId),
                        search(condition.getSearch())
                )
                .leftJoin(infoTalk.infoHashTags, infoHashTag).fetchJoin()
                .orderBy(infoTalk.id.desc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, results);
    }

    @Override
    public Slice<InfoTalk> findByIdGreaterThanOrderByIdAsc(InfoTalkSearchCondition condition, Long oldestInfoTalkId,
                                                           Pageable pageable) {
        List<InfoTalk> results = queryFactory
                .selectFrom(infoTalk)
                .where(
                        infoTalk.id.gt(oldestInfoTalkId),
                        search(condition.getSearch())
                )
                .leftJoin(infoTalk.infoHashTags, infoHashTag).fetchJoin()
                .orderBy(infoTalk.id.asc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, results);
    }

    @Override
    public Slice<InfoTalk> findByLoveLessThanOrderByLoveDesc(InfoTalkSearchCondition condition, Long id, int love,
                                                             Pageable pageable) {
        List<InfoTalk> result = queryFactory
                .selectFrom(infoTalk)
                .where(
                        infoTalk.love.lt(love).or(infoTalk.love.eq(love).and(infoTalk.id.lt(id))),
                        search(condition.getSearch())

                )
                .leftJoin(infoTalk.infoHashTags, infoHashTag).fetchJoin()
                .orderBy(infoTalk.love.desc(), infoTalk.id.desc())
                .limit(pageable.getOffset()+ 6)
                .fetch();

        return checkEndPage(pageable, result);
    }

    @Override
    public Slice<InfoTalk> findByViewLessThanOrderByViewDesc(InfoTalkSearchCondition condition, Long id, int view,
                                                             Pageable pageable) {
        List<InfoTalk> result = queryFactory
                .selectFrom(infoTalk)
                .where(
                        infoTalk.view.lt(view).or(infoTalk.view.eq(view).and(infoTalk.id.lt(id))),
                        search(condition.getSearch())
                )
                .leftJoin(infoTalk.infoHashTags, infoHashTag).fetchJoin()
                .orderBy(infoTalk.view.desc(), infoTalk.id.desc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, result);
    }
}
