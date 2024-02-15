package homeat.backend.domain.homeatreport.dto;

import homeat.backend.domain.homeatreport.entity.TierStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportTierNicknameResponseDTO {
    private TierStatus tierStatus;
    private String nickname;
}
