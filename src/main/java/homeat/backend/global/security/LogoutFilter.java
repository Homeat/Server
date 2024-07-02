package homeat.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonErrorStatus;
import homeat.backend.global.payload.CommonSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

    private final LoginService loginService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!(request.getMethod().equals("POST") && request.getRequestURI().matches("/v1/members/logout"))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String refreshToken = loginService.validateRefreshToken(request.getCookies());
//            Cookie nullCookie = loginService.revokeRefreshToken(refreshToken);
            loginService.revokeRefreshToken(refreshToken);

//            response.addCookie(nullCookie);
            writeOutput(request, response, HttpServletResponse.SC_OK, ApiPayload.onSuccess(CommonSuccessStatus.OK, null));
        } catch (Exception e) {
            writeOutput(request, response, HttpServletResponse.SC_BAD_REQUEST, ApiPayload.onFailure(CommonErrorStatus.BAD_REQUEST, null));
        }
    }

    private void writeOutput(HttpServletRequest request, HttpServletResponse response, int statusCode, ApiPayload<?> data) {
        try {
            response.setStatus(statusCode);
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(objectMapper.writeValueAsBytes(data));
        } catch (Exception e) {
            GeneralException newException = new GeneralException(LoginErrorStatus.OUTPUT_ERROR);
            request.setAttribute("exception", newException);
            throw newException;
        }
    }
}
