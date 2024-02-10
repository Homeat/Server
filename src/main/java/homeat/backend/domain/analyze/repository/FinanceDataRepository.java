package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.querydsl.FinanceDataRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceDataDataRepository extends JpaRepository<FinanceData, Long>, FinanceDataRepositoryCustom {
}