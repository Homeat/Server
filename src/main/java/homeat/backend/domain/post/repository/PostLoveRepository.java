package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.PostLove;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.user.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLoveRepository extends JpaRepository<PostLove, Long> {
    Optional<PostLove> findPostLoveByPostTypeAndMember(PostType postType, Member member);

    List<PostLove> findPostLoveByPostTypeAndMappingId(PostType postType, Long mappingId);
}
