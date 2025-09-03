package com.sprint.mission.discodeit.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.auth.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
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
        @Value("${jwt.access-token.secret}") String accessTokenSecret,
        @Value("${jwt.access-token.exp}") int accessTokenExpirationMs,
        @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${jwt.refresh-token.exp}") int refreshTokenExpirationMs
    ) throws JOSEException {
        log.info("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.debug("[TokenProvider] generateAccessToken 호출됨: {}의 Access Token 생성",
            userDetails.getUsername());
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.debug("[TokenProvider] generateRefreshToken 호출됨: {}의 Refresh Token 생성",
            userDetails.getUsername());
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    private String generateToken(DiscodeitUserDetails userDetails, int expirationMs,
        JWSSigner signer, String tokenType) throws JOSEException {
        log.debug("[TokenProvider] generateToken: {}의 {} 토큰 생성 시작", userDetails.getUsername(),
            tokenType);

        String tokenId = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userDetails.getUsername())
            .jwtID(tokenId)
            .claim("userId", userDetails.getUserId())
            .claim("type", tokenType)
            .claim("roles",
                userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
            )
            .issueTime(now)
            .expirationTime(expiryDate)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        String completedJWT = signedJWT.serialize();

        log.debug("[TokenProvider] generateToken 완료: {}의 {} 토큰={}", userDetails.getUsername(),
            tokenType, completedJWT);
        return completedJWT;
    }

    public Cookie generateRefreshTokenCookie(String refreshToken) {
        log.debug("[TokenProvider] generateRefreshTokenCookie 호출됨");
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);
        return cookie;
    }

    public Cookie generateRefreshTokenExpirationCookie() {
        log.debug("[TokenProvider] generateRefreshTokenExpirationCookie 호출됨");
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        log.debug("[TokenProvider] addRefreshCookie 호출됨");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);
        response.addCookie(cookie);
    }

    public void expireRefreshCookie(HttpServletResponse response) {
        log.debug("[TokenProvider] expireRefreshCookie 호출됨");
        Cookie cookie = generateRefreshTokenExpirationCookie();
        response.addCookie(cookie);
    }

    public boolean validateAccessToken(String token) {
        log.debug("[TokenProvider] validateAccessToken 호출됨");
        boolean result = verifyToken(token, accessTokenVerifier, "access");
        log.debug("[TokenProvider] validateAccessToken 결과={}", result);
        return result;
    }

    public boolean validateRefreshToken(String token) {
        log.debug("[TokenProvider] validateRefreshToken 호출됨");
        boolean result = verifyToken(token, refreshTokenVerifier, "refresh");
        log.debug("[TokenProvider] validateRefreshToken 결과={}", result);
        return result;
    }

    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (!signedJWT.verify(verifier)) {
                log.warn("[TokenProvider] verifyToken 실패: 서명 검증 실패");
                return false;
            }
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.warn("[TokenProvider] verifyToken 실패: 타입 불일치 (expected={}, actual={})",
                    expectedType, tokenType);
                return false;
            }
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean valid = exp != null && exp.after(new Date());
            return valid;
        } catch (Exception e) {
            log.error("[TokenProvider] verifyToken 예외 발생: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String getTokenId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Date getIssuedAt(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getIssueTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Date getExpiration(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public JwtTokenEntity toEntity(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            String username = signedJWT.getJWTClaimsSet().getSubject();
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            OffsetDateTime issuedAt = OffsetDateTime.ofInstant(
                signedJWT.getJWTClaimsSet().getIssueTime().toInstant(), ZoneOffset.UTC);
            OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(), ZoneOffset.UTC);

            return new JwtTokenEntity(jti, username, tokenType, issuedAt, expiresAt);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
