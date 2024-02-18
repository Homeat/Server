package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinanceDataRepository extends JpaRepository<FinanceData, Long>, FinanceDataRepositoryCustom {
    Optional<FinanceData> findByMember_Id(Long member_id);

    Optional<FinanceData> findTopByMember_IdOrderByCreatedAtDesc(Long member_id);
}