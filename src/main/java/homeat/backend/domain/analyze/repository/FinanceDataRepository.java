package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceDataRepository extends JpaRepository<FinanceData, Long>, FinanceDataRepositoryCustom {
}