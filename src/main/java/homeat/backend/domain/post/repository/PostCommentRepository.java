package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.PostComment;
import homeat.backend.domain.post.entity.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findPostCommentByPostTypeAndMappingId(PostType postType, Long mappingId);
}
