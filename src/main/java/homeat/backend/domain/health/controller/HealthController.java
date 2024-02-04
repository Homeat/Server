package homeat.backend.domain.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Health Check API")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "서버 상태 체크 api", responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(value = "I'm healthy!")
            }))
    })
    @GetMapping("")
    public String healthCheck() {
        return "I'm healthy!";
    }
}
