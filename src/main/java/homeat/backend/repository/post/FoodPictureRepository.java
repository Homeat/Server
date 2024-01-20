package homeat.backend.repository.post;

import homeat.backend.domain.post.FoodPicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodPictureRepository extends JpaRepository<FoodPicture, Long> {
}
