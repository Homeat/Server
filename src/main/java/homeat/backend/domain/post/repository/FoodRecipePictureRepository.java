package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodRecipePicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRecipePictureRepository extends JpaRepository<FoodRecipePicture, Long> {
}
