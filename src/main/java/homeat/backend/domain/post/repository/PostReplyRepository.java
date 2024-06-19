package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.PostReply;
import homeat.backend.domain.post.entity.PostType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReplyRepository extends JpaRepository<PostReply, Long> {
    List<PostReply> findPostRepliesByPostTypeAndMappingId(PostType postType, Long mappingId);
}
