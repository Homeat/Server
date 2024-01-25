package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRecipeRepository extends JpaRepository<FoodRecipe, Long> {
}
