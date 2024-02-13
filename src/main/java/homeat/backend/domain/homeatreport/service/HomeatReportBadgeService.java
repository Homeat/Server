package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportBadgeService {

    private final WeekRepository weekRepository;

    public ResponseEntity<ReportTierResponseDTO> getHomeatTier(Member member) {

    }
}
