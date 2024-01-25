package homeat.backend.domain.homeatreport.controller;

import homeat.backend.domain.homeatreport.service.HomeatReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/HomeatReport")
@RequiredArgsConstructor
public class HomeatReportController {

    private final HomeatReportService homeatReportService;

    /**
     * 홈잇뱃지 개수 조회
     */
    @GetMapping("{id}")
    public Long getNumHomeatBadge(@PathVariable("id")Long id) {
        return homeatReportService.getNumHomeatBadge(id);
    }
}
