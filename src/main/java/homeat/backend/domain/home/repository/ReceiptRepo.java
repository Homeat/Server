package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepo extends JpaRepository<Receipt, Long> {
    List<Receipt> findByDailyExpenseIdOrderByIdAsc(Long dailyExpenseId);
}
