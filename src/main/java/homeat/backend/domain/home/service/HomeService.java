package homeat.backend.domain.home.service;

import homeat.backend.domain.home.repository.DailyExpenseRepository;
import homeat.backend.domain.home.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final ReceiptRepository receiptRepository;

//    @Transactional
//    public ResponseEntity<?> saveExpense(ManagementRequestDTO dto) {
//
//    }


}