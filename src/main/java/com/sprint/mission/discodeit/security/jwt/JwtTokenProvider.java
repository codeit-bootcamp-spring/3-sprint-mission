package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.jwt.store.JwtTokenEntity;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
    public static final String PROVIDER_NAME = "[JwtTokenProvider] ";

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
            @Value("${jwt.access-token.exp}") int refreshTokenExpirationMs
    ) throws JOSEException {

        log.info(PROVIDER_NAME + "생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshTokenSecret);
        this.refreshTokenVerifier = new MACVerifier(refreshTokenSecret);
    }

    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info(PROVIDER_NAME + "generateAccessToken 호출됨: {} 의 액세스 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info(PROVIDER_NAME + "generateRefreshToken 호출됨: {} 의 리프레시 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    public String generateToken(DiscodeitUserDetails userDetails, int expirationMs, JWSSigner signer, String tokenType) throws JOSEException {

        log.info(PROVIDER_NAME + "generateToken: {}의 {} 토큰 생성 시작", userDetails.getUsername(), tokenType);

        String tokenId = UUID.randomUUID().toString();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .jwtID(tokenId)
                .claim("userId", userDetails.getUserDto().id())
                .claim("type", tokenType)
                .claim("roles",
                        userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
                .issueTime(now)
                .expirationTime(expirationDate)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        signedJWT.sign(signer);
        String completedJWT = signedJWT.serialize();

        log.info(PROVIDER_NAME + "generateToken: {}의 {} 생성 완료", userDetails.getUsername(), tokenType);
        return completedJWT;
    }

    public Cookie generateRefreshTokenCookie(String refreshToken) {

        log.info(PROVIDER_NAME + "generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);                // 개발환경: HTTP도 동작하도록 Secure=false (운영 환경에선 HTTPS 통신 이용 예정)
        cookie.setPath("/");
        cookie.setMaxAge(accessTokenExpirationMs / 1000);

        log.info(PROVIDER_NAME + "generateRefreshTokenCookie 완료: Max-Age= {}", (accessTokenExpirationMs / 1000));

        return cookie;
    }

    public Cookie generateRefreshTokenExpirationCookie() {

        log.info(PROVIDER_NAME + "generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);                // 쿠키 만료 시간을 0으로 설정하여 즉시 만료

        log.info(PROVIDER_NAME + "generateRefreshTokenExpirationCookie 완료: 리프레시 쿠키 만료");

        return cookie;
    }

    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        log.info(PROVIDER_NAME + "addRefreshDCookie 호출됨: Refresh Token 쿠키 응답에 추가");

        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    public void expireRefreshToken(HttpServletResponse response) {

        log.info(PROVIDER_NAME + "expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");

        Cookie cookie = generateRefreshTokenExpirationCookie();
    }

    public boolean validateAccessToken(String accessToken) {

        log.info(PROVIDER_NAME + "validateAccessToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(accessToken, accessTokenVerifier, "access");

        log.info(PROVIDER_NAME + "validateAccessToken 결과: {}", result);

        return result;
    }

    public boolean validateRefreshToken(String refreshToken) {

        log.info(PROVIDER_NAME + "validateRefreshToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(refreshToken, refreshTokenVerifier, "refresh");
        log.info(PROVIDER_NAME + "validateRefreshToken 결과: {}", result);

        return result;
    }

    private boolean verifyToken(String accessToken, JWSVerifier accessTokenVerifier, String expectedType) {

        try {
            // 토큰 파싱
            log.info(PROVIDER_NAME + "verifyToken: 토큰 파싱 시작");
            SignedJWT signedJWT = SignedJWT.parse(accessToken);

            // 서명 무결성 검증
            log.info(PROVIDER_NAME + "verifyToken: 서명 무결성 검증 시작");
            if (!signedJWT.verify(accessTokenVerifier)) {
                log.info(PROVIDER_NAME + "verifyToken: 서명 검증 실패");
                return false;
            }

            // 토큰 타입 검증
            log.info(PROVIDER_NAME + "verifyToken: 토큰 타입 검증 시작");
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.info(PROVIDER_NAME + "verifyToken: 타입 불일치 - expected = {}, actual = {}", expectedType, tokenType);
                return false;
            }

            // 만료 시간 검증
            log.info(PROVIDER_NAME + "verifyToken: 만료 시간 검증 시작");
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 만료 시간이 null이 아니고, 현재 시간보다 이후인 경우 유효(true)
            boolean valid = expirationTime != null && expirationTime.after(new Date());

            log.info(PROVIDER_NAME + "verifyToken: 만료 검사 결과= {}", valid);

            return valid;
        } catch (ParseException | JOSEException e) {
            log.info(PROVIDER_NAME + "verifyToken: 예외 발생 - " + e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            log.info(PROVIDER_NAME + "getUsernameFromToken 호출됨: subject 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.info(PROVIDER_NAME + "getUsernameFromToken 결과: subject= {}", subject);

            return subject;
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT", e);
        }
    }

    public String getTokenId(String token) {
        try {
            log.info(PROVIDER_NAME + "getTokenId 호출됨: jti 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            log.info(PROVIDER_NAME + "getTokenId 결과: jti= {}", jti);

            return jti;
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT", e);
        }
    }

    /**
     * 토큰에서 발급 시간(iat)을 추출한다.
     * 디버깅이나 감사 로그에서 토큰 생성 시점을 확인할 때 유용하다.
     *
     * @param token JWT 문자열
     * @return 발급 시간(Date)
     */
    public Date getIssuedAt(String token) {
        try {
            System.out.println("[TokenProvider] getIssuedAt 호출됨: iat 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date iat = signedJWT.getJWTClaimsSet().getIssueTime();

            System.out.println("[TokenProvider] getIssuedAt 결과: iat=" + iat);

            return iat;
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT", e);
        }
    }

    /**
     * 토큰에서 만료 시간(exp)을 추출한다.
     * 남은 유효 시간을 계산하거나 만료 임박 알림을 구현할 때 사용할 수 있다.
     *
     * @param token JWT 문자열
     * @return 만료 시간(Date)
     */
    public Date getExpiration(String token) {
        try {
            System.out.println("[TokenProvider] getExpiration 호출됨: exp 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            System.out.println("[TokenProvider] getExpiration 결과: exp=" + exp);

            return exp;
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT", e);
        }
    }

    public JwtTokenEntity toEntity(String accessToken) {
        try {
            log.info(PROVIDER_NAME + "toEntity 호출됨: 토큰 메타데이터 변환 시작");

            SignedJWT signedJWT = SignedJWT.parse(accessToken);

            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            String username = signedJWT.getJWTClaimsSet().getSubject();
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            OffsetDateTime issuedAt = OffsetDateTime.ofInstant(signedJWT.getJWTClaimsSet().getIssueTime().toInstant(), ZoneOffset.UTC);
            OffsetDateTime expiresAt = OffsetDateTime.ofInstant(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(), ZoneOffset.UTC);

            JwtTokenEntity entity = new JwtTokenEntity(jti, username, tokenType, issuedAt, expiresAt);

            return entity;
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT", e);
        }
    }
}


