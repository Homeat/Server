package homeat.backend.domain.temp.handler;

import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.BaseStatus;

public class TempHandler extends GeneralException {

    public TempHandler(BaseStatus errorStatus) {
        super(errorStatus);
    }
}
