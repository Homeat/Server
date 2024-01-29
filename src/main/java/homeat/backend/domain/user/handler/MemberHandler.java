package homeat.backend.domain.user.handler;

import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.BaseStatus;

public class MemberHandler extends GeneralException {

    public MemberHandler(BaseStatus errorStatus) {
        super(errorStatus);
    }
}
