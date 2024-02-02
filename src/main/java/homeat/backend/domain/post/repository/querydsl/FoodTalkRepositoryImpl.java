package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QFoodPicture.foodPicture;
import static homeat.backend.domain.post.entity.QFoodTalk.*;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.Tag;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class FoodTalkRepositoryImpl implements FoodTalkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FoodTalkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public FoodTalk findByFoodTalkId(Long id) {



        return queryFactory
                .select(foodTalk)
                .from(foodTalk)
                .join(foodTalk.foodPictures, foodPicture).fetchJoin()
                .where(foodTalk.id.eq(id))
                .fetchOne();

    }

    @Override
    public Slice<FoodTalk> findByIdLessThanOrderByIdDesc(FoodTalkSearchCondition condition, Long lastFoodTalkId,
                                                         Pageable pageable) {
        List<FoodTalk> results = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.id.lt(lastFoodTalkId),
                        nameEq(condition.getName()),
                        tagEq(condition.getTag())
                )
                .orderBy(foodTalk.id.desc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, results);

    }

    @Override
    public Slice<FoodTalk> findByIdGreaterThanOrderByIdAsc(FoodTalkSearchCondition condition, Long OldestFoodTalkId,
                                                           Pageable pageable) {

        List<FoodTalk> results = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.id.gt(OldestFoodTalkId),
                        nameEq(condition.getName()),
                        tagEq(condition.getTag())
                )
                .orderBy(foodTalk.id.asc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, results);
    }

    @Override
    public Slice<FoodTalk> findByLoveLessThanOrderByLoveDesc(FoodTalkSearchCondition condition ,Long id, int love, Pageable pageable) {
        List<FoodTalk> result = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.love.lt(love).or(foodTalk.love.eq(love).and(foodTalk.id.lt(id))),
                        nameEq(condition.getName()),
                        tagEq(condition.getTag())

                )
                .orderBy(foodTalk.love.desc(), foodTalk.id.desc())
                .limit(pageable.getOffset()+ 6)
                .fetch();

        return checkEndPage(pageable, result);

    }

    private Slice<FoodTalk> checkEndPage(Pageable pageable, List<FoodTalk> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize()); //한개더 가져왔으니 더 가져온 데이터 삭제
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<FoodTalk> findByViewLessThanOrderByViewDesc(FoodTalkSearchCondition condition,Long id, int view, Pageable pageable) {
        List<FoodTalk> result = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.view.lt(view).or(foodTalk.view.eq(view).and(foodTalk.id.lt(id))),
                        nameEq(condition.getName()),
                        tagEq(condition.getTag())
                )
                .orderBy(foodTalk.view.desc(), foodTalk.id.desc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, result);
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? foodTalk.name.eq(name) : null;

    }
    private BooleanExpression tagEq(String  tag) {
        return hasText(tag) ? foodTalk.tag.eq(Tag.valueOf(tag)) : null;

    }
}
