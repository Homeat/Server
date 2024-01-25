package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.HomeatReport;
import homeat.backend.domain.homeatreport.repository.HomeatReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportService {

    @Autowired
    private HomeatReportRepository homeatReportRepository;

    /**
     * 홈잇뱃지 개수 조회
     */
    public Long getNumHomeatBadge(Long id) {
        HomeatReport homeatReport = homeatReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 홈잇리포트를 찾을 수 없습니다."));
        return homeatReport.getTotal_success();
    }

}
