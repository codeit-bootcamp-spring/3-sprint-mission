package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds:3600}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds:86400}")
    private long refreshTokenValidityInSeconds;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /** 액세스 토큰 생성 */
    public String createAccessToken(String username) {
        return createToken(username, accessTokenValidityInSeconds);
    }

    /** 리프레시 토큰 생성 */
    public String createRefreshToken(String username) {
        return createToken(username, refreshTokenValidityInSeconds);
    }

    /** JWT 토큰 생성 (HS256) */
    private String createToken(String username, long validityInSeconds) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInSeconds * 1000);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(username)
            .issueTime(now)
            .expirationTime(exp)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8));
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("토큰 생성 중 오류가 발생했습니다.", e);
        }
    }

    /** 토큰에서 사용자명(subject) 추출 */
    public String getUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("토큰에서 사용자명을 추출하는 중 오류가 발생했습니다.", e);
        }
    }

    /** alias */
    public String getUsernameFromToken(String token) {
        return getUsername(token);
    }

    /** 토큰 유효성(서명+만료) 검증 */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            boolean isSignatureValid = signedJWT.verify(verifier);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean isTokenExpired = expirationTime != null && expirationTime.before(new Date());

            return isSignatureValid && !isTokenExpired;
        } catch (Exception e) {
            return false;
        }
    }

    /** 구분용 래퍼 */
    public boolean validateAccessToken(String token) { return validateToken(token); }
    public boolean validateRefreshToken(String token) { return validateToken(token); }

    /** 토큰으로부터 Authentication 생성 */
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
    }

    /** 리프레시 토큰 로테이션 결과(새 액세스/리프레시 발급) */
    public TokenPair refreshTokenRotation(String refreshToken) {
        if (!validateRefreshToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }
        String username = getUsername(refreshToken);
        String newAccessToken = createAccessToken(username);
        String newRefreshToken = createRefreshToken(username);
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    /** 요청 쿠키에서 REFRESH_TOKEN 추출 */
    public String resolveRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("REFRESH_TOKEN".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    /** 요청 헤더에서 Bearer 액세스 토큰 추출 */
    public String resolveAccessTokenFromHeader(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public long getAccessTokenValidityInSeconds() { return accessTokenValidityInSeconds; }
    public long getRefreshTokenValidityInSeconds() { return refreshTokenValidityInSeconds; }

    /** 컨트롤러에서 사용할 리프레시 토큰 쿠키 생성 */
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setHttpOnly(true);
        // 로컬 개발이 http면 false, 운영 HTTPS에선 true로 바꾸세요.
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenValidityInSeconds);
        return cookie;
    }

    /** 로그아웃/회수 시 즉시 만료 쿠키 */
    public Cookie generateExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie("REFRESH_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    /** 액세스/리프레시 토큰 쌍 */
    public record TokenPair(String accessToken, String refreshToken) {}
}
