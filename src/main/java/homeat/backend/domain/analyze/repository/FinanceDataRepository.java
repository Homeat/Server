package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceDataRepository extends JpaRepository<FinanceData, Long>, FinanceDataRepositoryCustom {
    Optional<FinanceData> findByMemberAndYearAndMonth(Member member, String year, String month);
    List<FinanceData> findTop2ByMemberOrderByCreatedAtDesc(Member member);

    Optional<FinanceData> findTopByMember_IdOrderByCreatedAtDesc(Long member_id);
    Optional<FinanceData> findFinanceDataById(Long financeDataId);
}