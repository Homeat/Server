package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoPictureRepository extends JpaRepository<InfoPicture, Long> {
}
