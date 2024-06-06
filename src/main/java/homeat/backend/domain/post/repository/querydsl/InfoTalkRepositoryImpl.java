package homeat.backend.domain.post.repository.querydsl;

import static homeat.backend.domain.post.entity.QInfoHashTag.infoHashTag;
import static homeat.backend.domain.post.entity.QInfoTalk.infoTalk;
import static homeat.backend.domain.post.entity.QPostComment.postComment;
import static homeat.backend.domain.post.entity.QPostPicture.postPicture;
import static homeat.backend.domain.post.entity.QPostReply.postReply;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.entity.Status;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class InfoTalkRepositoryImpl implements InfoTalkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public InfoTalkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private Slice<InfoTalkTotalView> checkEndPage(Pageable pageable, List<InfoTalkTotalView> results) {
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
    public Slice<InfoTalkTotalView> findByIdLessThanOrderByIdDesc(InfoTalkSearchCondition condition, Long lastInfoTalkId,
                                                                  Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(infoTalk.id,
                        infoTalk.createdAt,
                        infoTalk.updatedAt,
                        infoTalk.title,
                        infoTalk.content,
                        postPicture.url,
                        infoTalk.love,
                        infoTalk.view,
                        infoTalk.commentNumber)
                .from(infoTalk)
                .join(postPicture).on(infoTalk.id.eq(postPicture.mappingId))
                .leftJoin(infoHashTag).on(infoTalk.id.eq(infoHashTag.infoTalk.id)).fetchJoin()
                .where(
                        infoTalk.id.lt(lastInfoTalkId),
                        infoTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        postPicture.postType.eq(PostType.InfoTalk)
                )
                .groupBy(infoTalk.id)
                .orderBy(infoTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<InfoTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new InfoTalkTotalView(tuple.get(infoTalk.id), tuple.get(infoTalk.createdAt), tuple.get(infoTalk.updatedAt),
                                tuple.get(infoTalk.title), tuple.get(infoTalk.content), tuple.get(postPicture.url),
                                tuple.get(infoTalk.love), tuple.get(infoTalk.view), tuple.get(infoTalk.commentNumber))
                )
                .collect(Collectors.toList());


        return checkEndPage(pageable, content);
    }

    @Override
    public Slice<InfoTalkTotalView> findByIdGreaterThanOrderByIdAsc(InfoTalkSearchCondition condition, Long oldestInfoTalkId,
                                                           Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(infoTalk.id,
                        infoTalk.createdAt,
                        infoTalk.updatedAt,
                        infoTalk.title,
                        infoTalk.content,
                        postPicture.url,
                        infoTalk.love,
                        infoTalk.view,
                        infoTalk.commentNumber)
                .from(infoTalk)
                .join(postPicture).on(infoTalk.id.eq(postPicture.mappingId))
                .leftJoin(infoHashTag).on(infoTalk.id.eq(infoHashTag.infoTalk.id)).fetchJoin()
                .where(
                        infoTalk.id.gt(oldestInfoTalkId),
                        infoTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        postPicture.postType.eq(PostType.InfoTalk)
                )
                .groupBy(infoTalk.id)
                .orderBy(infoTalk.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<InfoTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new InfoTalkTotalView(tuple.get(infoTalk.id), tuple.get(infoTalk.createdAt), tuple.get(infoTalk.updatedAt),
                                tuple.get(infoTalk.title), tuple.get(infoTalk.content), tuple.get(postPicture.url),
                                tuple.get(infoTalk.love), tuple.get(infoTalk.view), tuple.get(infoTalk.commentNumber))
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);
    }

    @Override
    public Slice<InfoTalkTotalView> findByLoveLessThanOrderByLoveDesc(InfoTalkSearchCondition condition, Long id, int love,
                                                             Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(infoTalk.id,
                        infoTalk.createdAt,
                        infoTalk.updatedAt,
                        infoTalk.title,
                        infoTalk.content,
                        postPicture.url,
                        infoTalk.love,
                        infoTalk.view,
                        infoTalk.commentNumber)
                .from(infoTalk)
                .join(postPicture).on(infoTalk.id.eq(postPicture.mappingId))
                .leftJoin(infoHashTag).on(infoTalk.id.eq(infoHashTag.infoTalk.id)).fetchJoin()
                .where(
                        infoTalk.love.lt(love).or(infoTalk.love.eq(love).and(infoTalk.id.lt(id))),
                        infoTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        postPicture.postType.eq(PostType.InfoTalk)

                )
                .groupBy(infoTalk.id)
                .orderBy(infoTalk.love.desc(), infoTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<InfoTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new InfoTalkTotalView(tuple.get(infoTalk.id), tuple.get(infoTalk.createdAt), tuple.get(infoTalk.updatedAt),
                                tuple.get(infoTalk.title), tuple.get(infoTalk.content), tuple.get(postPicture.url),
                                tuple.get(infoTalk.love), tuple.get(infoTalk.view), tuple.get(infoTalk.commentNumber))
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);
    }

    @Override
    public Slice<InfoTalkTotalView> findByViewLessThanOrderByViewDesc(InfoTalkSearchCondition condition, Long id, int view,
                                                             Pageable pageable) {
        QueryResults<Tuple> result = queryFactory
                .select(infoTalk.id,
                        infoTalk.createdAt,
                        infoTalk.updatedAt,
                        infoTalk.title,
                        infoTalk.content,
                        postPicture.url,
                        infoTalk.love,
                        infoTalk.view,
                        infoTalk.commentNumber)
                .from(infoTalk)
                .join(postPicture).on(infoTalk.id.eq(postPicture.mappingId))
                .leftJoin(infoHashTag).on(infoTalk.id.eq(infoHashTag.infoTalk.id)).fetchJoin()
                .where(
                        infoTalk.view.lt(view).or(infoTalk.view.eq(view).and(infoTalk.id.lt(id))),
                        infoTalk.status.eq(Status.저장),
                        search(condition.getSearch()),
                        postPicture.postType.eq(PostType.InfoTalk)
                )
                .groupBy(infoTalk.id)
                .orderBy(infoTalk.view.desc(), infoTalk.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetchResults();

        List<InfoTalkTotalView> content = result.getResults().stream()
                .map(tuple ->
                        new InfoTalkTotalView(tuple.get(infoTalk.id), tuple.get(infoTalk.createdAt), tuple.get(infoTalk.updatedAt),
                                tuple.get(infoTalk.title), tuple.get(infoTalk.content), tuple.get(postPicture.url),
                                tuple.get(infoTalk.love), tuple.get(infoTalk.view), tuple.get(infoTalk.commentNumber))
                )
                .collect(Collectors.toList());

        return checkEndPage(pageable, content);
    }

    @Override
    public Long countTotalCommentNumber(Long infoTalkId) {
        return queryFactory
                .select(postComment.count())
                .from(postComment)
                .where(postComment.postType.eq(PostType.InfoTalk).and(postComment.mappingId.eq(infoTalkId)))
                .fetchOne();
    }

    @Override
    public Long countTotalReplyNumber(Long infoTalkCommentId) {
        return queryFactory
                .select(postReply.count())
                .from(postReply)
                .where(postReply.postType.eq(PostType.InfoTalk).and(postReply.mappingId.eq(infoTalkCommentId)))
                .fetchOne();
    }
}
