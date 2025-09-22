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
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
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

        log.debug("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

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

        log.debug("[TokenProvider] generateAccessToken 호출됨: {}의 엑세스 토큰 생성",
            userDetails.getUsername());

        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.debug("[TokenProvider] generateRefreshToken 호출됨: {}의 리프레시 토큰 생성",
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
            .claim("userId", userDetails.userId())
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

        log.debug("[TokenProvider] generateToken: {}의 {} 토큰 생성 완료: {}", userDetails.getUsername(),
            tokenType, completedJWT);

        return completedJWT;
    }

    public Cookie generateRefreshTokenCookie(String refreshToken) {

        log.debug("[TokenProvider] generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.debug("[TokenProvider] generateRefreshTokenCookie 완료: Max-Age={}",
            refreshTokenExpirationMs / 1000);

        return cookie;
    }

    public Cookie generateRefreshTokenExpirationCookie() {

        log.debug(
            "[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        log.debug("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }

    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        log.debug("[TokenProvider] addRefreshCookie 호출됨: RT 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    public void expireRefreshCookie(HttpServletResponse response) {

        log.debug("[TokenProvider] expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }

    public boolean validateAccessToken(String token) {

        log.debug("[TokenProvider] validateAccessToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, accessTokenVerifier, "access");
        log.debug("[TokenProvider] validateAccessToken 결과: {}", result);

        return result;
    }

    public boolean validateRefreshToken(String token) {

        log.debug("[TokenProvider] validateRefreshToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, refreshTokenVerifier, "refresh");
        log.debug("[TokenProvider] validateRefreshToken 결과: " + result);

        return result;
    }

    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {

        try {
            log.debug(("[TokenProvider] verifyToken: 토큰 파싱 시작"));

            SignedJWT signedJWT = SignedJWT.parse(token);

            log.debug("[TokenProvider] verifyToken: 서명 무결성 검증 시작");
            if (!signedJWT.verify(verifier)) {
                log.debug("[TokenProvider] verifyToken: 서명 검증 실패");
                return false;
            }

            log.debug("[TokenProvider] verifyToken: 토큰 타입 검증 시작");
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.debug("[TokenProvider] verifyToken: 타입 불일치 - expected={}, actual={}",
                    expectedType, tokenType);
                return false;
            }

            log.debug("[TokenProvider] verifyToken: 만료 시간 검증 시작");
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean valid = exp != null && exp.after(new Date());

            log.debug("[TokenProvider] verifyToken: 만료 검사 결과={}", valid);

            return valid;
        } catch (JOSEException | ParseException e) {
            log.debug("[TokenProvider] verifyToken: 예외 발생 - {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            log.debug("[TokenProvider] getUsernameFromToken 호출됨: subject 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.debug("[TokenProvider] getUsernameFromToken 결과: subject=" + subject);

            return subject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }


}
