package homeat.backend.repository.post;

import homeat.backend.domain.post.FoodPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodPictureRepository extends JpaRepository<FoodPicture, Long> {
}
