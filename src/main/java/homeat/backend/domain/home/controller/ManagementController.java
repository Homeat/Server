package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.dto.ManagementRequestDTO;
import homeat.backend.domain.home.service.ManagementService;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class ManagementController {

    private final ManagementService managementService;

    /**
     * 목표 금액 저장
     */
//    @PostMapping("/save")
//    public ResponseEntity<?> saveExpense(@RequestBody ManagementRequestDTO dto) { return  managementService.saveExpense(dto); }

    /**
     * 홈 화면 조회
     */
//    @GetMapping("/")
}