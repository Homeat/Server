package homeat.backend.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@JsonPropertyOrder({"isSuccess", "code", "message", "pageIdx", "pageSize", "hasNext", "data"})
public class SlicePayload<T> {

    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private final int pageIdx;
    private final int pageSize;
    private final Boolean hasNext;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> data;

    public static <T> SlicePayload<T> onSuccess(BaseStatus status, Slice<T> sliceData) {
        return new SlicePayload<>(true, status.getReason().getCode(), status.getReason().getMessage(), sliceData.getNumber(), sliceData.getSize(), sliceData.hasNext(), sliceData.getContent());
    }
}