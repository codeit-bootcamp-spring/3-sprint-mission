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
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(
        @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
        @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
        @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs
    ) throws JOSEException {

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

    /** 액세스 토큰 발급 */
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    /** 리프레시 토큰 발급 */
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    /** 액세스 토큰 유효성 검사 */
    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenVerifier, "access");
    }

    /** 리프레시 토큰 유효성 검사 */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenVerifier, "refresh");
    }

    /** 토큰에서 username(sub) 추출 */
    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 JWT 토큰입니다.", e);
        }
    }

    /** 토큰에서 JWT ID 추출 */
    public String getTokenId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 JWT 토큰입니다.", e);
        }
    }

    /** HttpOnly 쿠키(리프레시 토큰) 생성 */
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);   // 운영 환경에서 HTTPS 권장
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(refreshTokenExpirationMs / 1000);
        return refreshCookie;
    }

    /** 리프레시 토큰 만료(삭제) 쿠키 생성 */
    public Cookie generateRefreshTokenExpirationCookie() {
        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);   // 운영 환경에서 HTTPS 권장
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        return refreshCookie;
    }

    // ===== 내부 공통 메서드 =====

    private String generateToken(
        DiscodeitUserDetails userDetails,
        int expirationMs,
        JWSSigner signer,
        String tokenType
    ) throws JOSEException {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expirationMs);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .subject(userDetails.getUsername())
            .issuer("discodeit")
            .issueTime(Date.from(now))
            .expirationTime(Date.from(exp))
            // 커스텀 클레임
            .claim("type", tokenType)               // access | refresh
            .claim("uid", userDetails.getUserDto().id().toString())
            .claim("role", "ROLE_" + userDetails.getUserDto().role().name())
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader(JWSAlgorithm.HS256),
            claims
        );
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 1) 서명 검증
            if (!signedJWT.verify(verifier)) {
                log.debug("JWT 서명 검증 실패: {}", expectedType);
                return false;
            }

            // 2) 토큰 타입 확인
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.debug("JWT 토큰 타입 불일치: 기대={}, 실제={}", expectedType, tokenType);
                return false;
            }

            // 3) 만료 확인
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (exp == null || exp.before(new Date())) {
                log.debug("JWT 토큰 만료: {}", expectedType);
                return false;
            }

            return true;
        } catch (Exception e) {
            log.debug("JWT 파싱/검증 오류: {}", e.getMessage());
            return false;
        }
    }
}