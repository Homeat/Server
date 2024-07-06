package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportBadgeInfoResponseDTO {

    private String homeatTier;
    private String nickname;

}
