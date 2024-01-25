package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.HomeatReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public interface HomeatReportRepository extends JpaRepository<HomeatReport, Long> {
}
