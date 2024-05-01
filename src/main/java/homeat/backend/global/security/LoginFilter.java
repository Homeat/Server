package homeat.backend.global.security;

import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Refresh;
import homeat.backend.domain.user.repository.RefreshRepository;
import homeat.backend.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

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
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
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
}
