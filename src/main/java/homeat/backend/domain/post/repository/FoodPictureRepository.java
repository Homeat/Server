package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodPictureRepository extends JpaRepository<FoodPicture, Long> {
}
