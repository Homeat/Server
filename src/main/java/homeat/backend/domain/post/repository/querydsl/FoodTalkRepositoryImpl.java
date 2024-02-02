package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QFoodPicture.foodPicture;
import static homeat.backend.domain.post.entity.QFoodTalk.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.entity.FoodTalk;
import javax.persistence.EntityManager;

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
}