package homeat.backend.global.security.jwt;

import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private final Long expiredMs = 1000 * 60 * 60 * 24 * 365L;
    private final MemberRepository memberRepository;

    public JwtUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String createJwt(Long memberId) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Date getExpiredAt(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.split(" ")[1];
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new GeneralException(JwtErrorStatus.EXPIRED_TOKEN);
        } catch(IllegalArgumentException e) {
            throw new GeneralException(JwtErrorStatus.NOT_FOUND_TOKEN);
        } catch (Exception e) {
            throw new GeneralException(JwtErrorStatus.INVALID_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {
        Long memberId = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().get("memberId", Long.class);

        if (!memberRepository.existsById(memberId)) {
           throw new GeneralException(JwtErrorStatus.NOT_FOUND_ID);
        }

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        User principal = new User(memberId.toString(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
