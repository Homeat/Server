package homeat.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Refresh;
import homeat.backend.domain.user.repository.RefreshRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import homeat.backend.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;

        setFilterProcessesUrl("/v1/members/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginDTO loginDTO = new LoginDTO();

        try {
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDTO = objectMapper.readValue(messageBody, LoginDTO.class);
        } catch (IOException e) {
            writeOutput(request, response, HttpServletResponse.SC_BAD_REQUEST, ApiPayload.onFailure(LoginErrorStatus.INVALID_PARAMETER, null));
            return null;
        }

        String username = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = customUserDetails.getUserId();

        String accessToken = jwtUtil.createJwt("access", userId, 60*60*10L);
        String refreshToken = jwtUtil.createJwt("refresh", userId, 24*60*60*10L);

        addRefreshEntity(userId, refreshToken, 24*60*60*10L);

        response.addHeader("Access-Token", accessToken);
        response.addCookie(createCookie("Refresh-Token", refreshToken));
        writeOutput(request, response, HttpServletResponse.SC_OK, ApiPayload.onSuccess(CommonSuccessStatus.OK, null));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        writeOutput(request, response, HttpServletResponse.SC_UNAUTHORIZED, ApiPayload.onFailure(LoginErrorStatus.LOGIN_FAILED, null));
    }

    private void addRefreshEntity(Long userId, String refreshToken, Long expiredMs) {

        LocalDateTime date = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expiredMs/1000L);
        System.currentTimeMillis();
        Refresh newRefresh = Refresh.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredAt(date)
                .build();
        refreshRepository.save(newRefresh);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
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
