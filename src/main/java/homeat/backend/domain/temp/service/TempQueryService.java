package homeat.backend.domain.temp.service;

import homeat.backend.domain.temp.handler.TempErrorStatus;
import homeat.backend.domain.temp.handler.TempHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempQueryService {

    public void CheckFlag(Integer flag) {
        if (flag == 1) {
            throw new TempHandler(TempErrorStatus.TEMP_EXCEPTION);
        }
    }
}
