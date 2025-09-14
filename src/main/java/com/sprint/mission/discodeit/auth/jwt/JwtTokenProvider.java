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
import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    // 리프레시 토큰을 저장할 HTTP 쿠키의 이름
    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

     // 구성 프로퍼티를 기반으로 토큰 서명/검증자와 만료 시간을 초기화한다.
    public JwtTokenProvider(
        @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
        @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
        @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs
    ) throws JOSEException {

        log.info("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

        // 주입받은 만료 시간 값들을 필드에 저장하여 토큰 생성 시 사용할 수 있도록 설정
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        // 액세스 토큰용 비밀키를 바이트 배열로 변환하여 HMAC-SHA256 서명자와 검증자를 생성
        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        // 리프레시 토큰용 비밀키를 바이트 배열로 변환하여 별도의 서명자와 검증자를 생성
        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

     // 액세스 토큰 생성
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info("[TokenProvider] generateAccessToken 호출됨: {} 의 엑세스 토큰 생성 ", userDetails.getUsername());

        // 생성할 토큰의 타입이 "access"인 경우 액세스 토큰을 생성한다.
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }


    // 리프레시 토큰 생성
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info("[TokenProvider] generateRefreshToken 호출됨: {} 의 리프레시 토큰 생성 ", userDetails.getUsername());

        // 생성할 토큰의 타입이 "refresh"인 경우 리프레시 토큰을 생성한다.
        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }


     //토큰 생성
    private String generateToken(DiscodeitUserDetails userDetails, int expirationMs, JWSSigner signer,
        String tokenType) throws JOSEException {

        log.info("[TokenProvider] generateToken: {}의 {} 토큰 생성 시작", userDetails.getUsername(), tokenType);

        // 토큰의 고유 식별자(jti)로 사용할 랜덤 UUID를 생성한다.
        String tokenId = UUID.randomUUID().toString();
        UserDto user = userDetails.getUserDto();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        // 토큰의 클레임(claims)을 설정한다.
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(user.username())
            .jwtID(tokenId)
            .claim("userId", user.id().toString())
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

        // 토큰 생성: 준비된 클레임과 헤더(HS256)를 사용하여 토큰 생성.
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // 토큰 서명: 생성된 토큰에 서명자를 적용하여 서명.
        signedJWT.sign(signer);

        // 토큰 직렬화: 실질적으로 JWT 토큰을 생성한 후 URL 안전한 문자열(Base64 인코딩)로 직렬화하는 메서드.
        String token = signedJWT.serialize();

        log.info("[TokenProvider] generateToken: {} 의 {} 토큰 생성 완료: {}", userDetails.getUsername(), tokenType, token);

        return token;
    }


    // 리프레시 토큰을 HttpOnly 쿠키로 생성
    public Cookie generateRefreshTokenCookie(String refreshToken) {

        log.info("[TokenProvider] generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        // 개발 환경: HTTP도 동작하도록 Secure=false
        // (운영 환경은 true를 사용해 HTTPS 통신을 이용할 수 있도록 권장)
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.info("[TokenProvider] generateRefreshTokenCookie 완료: Max-Age= {}", (refreshTokenExpirationMs / 1000));

        return cookie;
    }


    // 리프레시 토큰 쿠키를 즉시 만료시키는 쿠키를 생성
    public Cookie generateRefreshTokenExpirationCookie() {

        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }


     // 리프레시 토큰을 담은 HttpOnly 쿠키를 응답에 추가
    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        log.info("[TokenProvider] addRefreshCookie 호출됨: RT 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    // 만료 쿠키를 응답에 추가
    public void expireRefreshCookie(HttpServletResponse response) {

        log.debug("[TokenProvider] expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }

    public boolean validateAccessToken(String token) {
        return verifyToken(token, accessTokenVerifier, "access");
    }

    public boolean validateRefreshToken(String token) {
        return verifyToken(token, refreshTokenVerifier, "refresh");
    }


    // 토큰의 서명과 클레임을 실제로 검증하는 내부 유틸리티 메서드
    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {

        try {
            // 토큰 파싱
            log.info("[TokenProvider] verifyToken: 토큰 파싱 시작");
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 무결성 검증
            log.info("[TokenProvider] verifyToken: 서명 무결성 검증 시작");
            if (!signedJWT.verify(verifier)) {
                log.warn("[TokenProvider] verifyToken: 서명 검증 실패");
                return false;
            }

            // 토큰 타입 검증
            log.info("[TokenProvider] verifyToken: 토큰 타입 검증 시작");
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.warn("[TokenProvider] verifyToken: 타입 불일치 - expected= {}, actual= {} ", expectedType, tokenType);
                return false;
            }

            // 만료 시간 검증
            log.info("[TokenProvider] verifyToken: 만료 시간 검증 시작");
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 만료 시간이 null이 아니고, 현재 시간보다 이후인 경우 유효(true)
            boolean valid = exp != null && exp.after(new Date());

            // 위의 모든 검증이 성공하면 true, 하나라도 실패하면 false
            log.info("[TokenProvider] verifyToken: 만료 검사 결과= {}", valid);

            return valid;
        } catch (Exception e) {
            log.error("[TokenProvider] verifyToken: 예외 발생 - {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            log.debug("[TokenProvider] username 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.debug("[TokenProvider] getUsernameFromToken 결과: {}", subject);
            return subject;

        } catch (Exception e) {
            throw new IllegalArgumentException("[TokenProvider] getUsernameFromToken: 예외 발생 - {}", e);
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            log.debug("[TokenProvider] userId 추출 시작");
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userIdStr = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
            if (userIdStr == null) {
                throw new IllegalArgumentException("[TokenProvider] JWT 토큰에서 User Id 클레임 찾을 수 없음");
            }
            return UUID.fromString(userIdStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("[TokenProvider] getUserIdFromToken: 예외 발생 - {}", e);
        }
    }

}