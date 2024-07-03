package homeat.backend.global.security;

import homeat.backend.domain.user.entity.Refresh;
import homeat.backend.domain.user.repository.RefreshRepository;
import homeat.backend.global.exception.GeneralException;
import homeat.backend.global.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Value("${JWT.ACCESS.EXPIRE}")
    private Long accessExpirationTime;

    @Value("${JWT.REFRESH.EXPIRE}")
    private Long refreshExpirationTime;

    public String issueAccessToken(Long userId) {
        String accessToken = jwtUtil.createJwt("access", userId, accessExpirationTime*1000L);
        return "Bearer " + accessToken;
    }

//    @Transactional
//    public Cookie issueRefreshToken(Long userId) {
//        String refreshToken = jwtUtil.createJwt("refresh", userId, refreshExpirationTime*1000L);
//        saveRefreshToken(userId, refreshToken, refreshExpirationTime);
//        return createCookie("refresh", refreshToken, refreshExpirationTime.intValue());
//    }
//
//    @Transactional
//    public Cookie reissueRefreshToken(Long userId, String refreshToken) {
//        refreshRepository.deleteByRefreshToken(refreshToken);
//        String newRefreshToken = jwtUtil.createJwt("refresh", userId, refreshExpirationTime*1000L);
//        saveRefreshToken(userId, newRefreshToken, refreshExpirationTime);
//        return createCookie("refresh", newRefreshToken, refreshExpirationTime.intValue());
//    }
//
//    @Transactional
//    public Cookie revokeRefreshToken(String refreshToken) {
//        refreshRepository.deleteByRefreshToken(refreshToken);
//        return createCookie("refresh", null, 0);
//    }

    @Transactional
    public String issueRefreshToken(Long userId) {
        String refreshToken = jwtUtil.createJwt("refresh", userId, refreshExpirationTime*1000L);
        saveRefreshToken(userId, refreshToken, refreshExpirationTime);
        return refreshToken;
    }

    @Transactional
    public String reissueRefreshToken(Long userId, String refreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
        String newRefreshToken = jwtUtil.createJwt("refresh", userId, refreshExpirationTime*1000L);
        saveRefreshToken(userId, newRefreshToken, refreshExpirationTime);
        return newRefreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
    }

    public String validateRefreshToken(Cookie[] cookies) {
        String refreshToken = null;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("refresh"))
                refreshToken = cookie.getValue();

        if (refreshToken == null)
            throw new GeneralException(LoginErrorStatus.NOT_FOUND_TOKEN);

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(LoginErrorStatus.EXPIRED_TOKEN);
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh"))
            throw new GeneralException(LoginErrorStatus.INVALID_TOKEN);

        Boolean isExist = refreshRepository.existsByRefreshToken(refreshToken);
        if (!isExist)
            throw new GeneralException(LoginErrorStatus.INVALID_TOKEN);

        return refreshToken;
    }

    private void saveRefreshToken(Long userId, String refreshToken, Long expirationTime) {
        LocalDateTime date = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(expirationTime);
        Refresh newRefresh = Refresh.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredAt(date)
                .build();

        refreshRepository.save(newRefresh);
    }

//    private Cookie createCookie(String key, String value, int expiry) {
//        Cookie cookie = new Cookie(key, value);
//        cookie.setMaxAge(expiry);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//
//        return cookie;
//    }
}
