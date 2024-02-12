package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.Badge_img;
import homeat.backend.domain.homeatreport.repository.BadgeImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BadgeImgService {

    private final BadgeImgRepository badgeImgRepository;
}
