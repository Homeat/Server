package homeat.backend.repository.post;

import homeat.backend.domain.post.InfoPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoPictureRepository extends JpaRepository<InfoPicture, Long> {
}
