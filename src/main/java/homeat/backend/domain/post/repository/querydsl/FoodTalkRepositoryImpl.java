package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QFoodPicture.foodPicture;
import static homeat.backend.domain.post.entity.QFoodTalk.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.entity.FoodTalk;
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
    public Slice<FoodTalk> findByLoveLessThanOrderByLoveDesc(Long id, int love, Pageable pageable) {
        List<FoodTalk> result = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.love.lt(love).or(foodTalk.love.eq(love).and(foodTalk.id.lt(id)))
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
    public Slice<FoodTalk> findByViewLessThanOrderByViewDesc(Long id, int view, Pageable pageable) {
        List<FoodTalk> result = queryFactory
                .selectFrom(foodTalk)
                .where(
                        foodTalk.view.lt(view).or(foodTalk.view.eq(view).and(foodTalk.id.lt(id)))
                )
                .orderBy(foodTalk.view.desc(), foodTalk.id.desc())
                .limit(pageable.getOffset() + 6)
                .fetch();

        return checkEndPage(pageable, result);
    }
}
