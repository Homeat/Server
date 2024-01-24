package homeat.backend.domain.home.service;

import homeat.backend.domain.home.dto.ManagementRequestDTO;
import homeat.backend.domain.home.entity.Management;
import homeat.backend.domain.home.repository.ManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementRepository managementRepository;

//    @Transactional
//    public ResponseEntity<?> saveExpense(ManagementRequestDTO dto) {
//
//    }


}
