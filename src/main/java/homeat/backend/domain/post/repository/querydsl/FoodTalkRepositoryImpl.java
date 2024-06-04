package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QFoodTalk.*;
import static homeat.backend.domain.post.entity.QFoodTalkComment.foodTalkComment;
import static homeat.backend.domain.post.entity.QFoodTalkReply.foodTalkReply;
import static homeat.backend.domain.post.entity.QPostPicture.*;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.entity.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class FoodTalkRepositoryImpl implements FoodTalkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FoodTalkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Slice<FoodTalkTotalView> findByIdLessThanOrderByIdDesc(FoodTalkSearchCondition condition, Long lastFoodTalkId,
                                                         Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(
                        foodTalk.id,
                        postPicture.url,
                        foodTalk.name,
                        foodTalk.view,
                        foodTalk.love
                )
                .from(foodTalk)
                .join(postPicture).on(foodTalk.id.eq(postPicture.mappingId))
                .where(
                        foodTalk.id.lt(lastFoodTalkId),
                        foodTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        tagEq(condition.getTag()),
                        postPicture.postType.eq(PostType.FoodTalk)
                )
                .groupBy(foodTalk.id)
                .orderBy(foodTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<FoodTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new FoodTalkTotalView(
                                tuple.get(foodTalk.id),
                                tuple.get(postPicture.url),
                                tuple.get(foodTalk.name),
                                tuple.get(foodTalk.view),
                                tuple.get(foodTalk.love)
                        )
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);

    }

    @Override
    public Slice<FoodTalkTotalView> findByIdGreaterThanOrderByIdAsc(FoodTalkSearchCondition condition, Long OldestFoodTalkId,
                                                           Pageable pageable) {

        QueryResults<Tuple> result = queryFactory
                .select(
                        foodTalk.id,
                        postPicture.url,
                        foodTalk.name,
                        foodTalk.view,
                        foodTalk.love
                )
                .from(foodTalk)
                .join(postPicture).on(foodTalk.id.eq(postPicture.mappingId))
                .where(
                        foodTalk.id.gt(OldestFoodTalkId),
                        foodTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        tagEq(condition.getTag()),
                        postPicture.postType.eq(PostType.FoodTalk)
                )
                .groupBy(foodTalk.id)
                .orderBy(foodTalk.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<FoodTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new FoodTalkTotalView(
                                tuple.get(foodTalk.id),
                                tuple.get(postPicture.url),
                                tuple.get(foodTalk.name),
                                tuple.get(foodTalk.view),
                                tuple.get(foodTalk.love)
                        )
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);
    }

    @Override
    public Slice<FoodTalkTotalView> findByLoveLessThanOrderByLoveDesc(FoodTalkSearchCondition condition ,Long id, int love, Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(
                        foodTalk.id,
                        postPicture.url,
                        foodTalk.name,
                        foodTalk.view,
                        foodTalk.love
                )
                .from(foodTalk)
                .join(postPicture).on(foodTalk.id.eq(postPicture.mappingId))
                .where(
                        foodTalk.love.lt(love).or(foodTalk.love.eq(love).and(foodTalk.id.lt(id))),
                        foodTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        tagEq(condition.getTag()),
                        postPicture.postType.eq(PostType.FoodTalk)

                )
                .groupBy(foodTalk.id)
                .orderBy(foodTalk.love.desc(), foodTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<FoodTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new FoodTalkTotalView(
                                tuple.get(foodTalk.id),
                                tuple.get(postPicture.url),
                                tuple.get(foodTalk.name),
                                tuple.get(foodTalk.view),
                                tuple.get(foodTalk.love)
                        )
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);

    }

    private Slice<FoodTalkTotalView> checkEndPage(Pageable pageable, List<FoodTalkTotalView> results) {
        boolean hasNext = false;
        if(results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize()); //한개더 가져왔으니 더 가져온 데이터 삭제
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public Slice<FoodTalkTotalView> findByViewLessThanOrderByViewDesc(FoodTalkSearchCondition condition,Long id, int view, Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(
                        foodTalk.id,
                        postPicture.url,
                        foodTalk.name,
                        foodTalk.view,
                        foodTalk.love
                )
                .from(foodTalk)
                .join(postPicture).on(foodTalk.id.eq(postPicture.mappingId))
                .where(
                        foodTalk.view.lt(view).or(foodTalk.view.eq(view).and(foodTalk.id.lt(id))),
                        foodTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        tagEq(condition.getTag()),
                        postPicture.postType.eq(PostType.FoodTalk)
                )
                .groupBy(foodTalk.id)
                .orderBy(foodTalk.view.desc(), foodTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<FoodTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new FoodTalkTotalView(
                                tuple.get(foodTalk.id),
                                tuple.get(postPicture.url),
                                tuple.get(foodTalk.name),
                                tuple.get(foodTalk.view),
                                tuple.get(foodTalk.love)
                        )
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);
    }

    @Override
    public Long countTotalCommentNumber(Long foodTalkId) {
        return queryFactory
                .select(foodTalkComment.count())
                .from(foodTalkComment)
                .where(foodTalkComment.foodTalk.id.eq(foodTalkId))
                .fetchOne();
    }

    @Override
    public Long countTotalReplyNumber(Long foodTalkCommentId) {
        return queryFactory
                .select(foodTalkReply.count())
                .from(foodTalkReply)
                .where(foodTalkReply.foodTalkComment.id.eq(foodTalkCommentId))
                .fetchOne();
    }

    private BooleanExpression search(String search) {
        return hasText(search) ? foodTalk.name.contains(search).or(foodTalk.memo.contains(search)) : null;

    }
    private BooleanExpression tagEq(String  tag) {
        return hasText(tag) ? foodTalk.tag.eq(Tag.valueOf(tag)) : null;

    }
}
