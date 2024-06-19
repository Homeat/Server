package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.PostPicture;
import homeat.backend.domain.post.entity.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostPictureRepository extends JpaRepository<PostPicture, Long> {
    List<PostPicture> findPostPictureByPostTypeAndMappingId(PostType postType, Long mappingId);
}
