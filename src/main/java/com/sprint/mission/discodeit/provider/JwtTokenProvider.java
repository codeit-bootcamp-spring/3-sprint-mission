package com.sprint.mission.discodeit.provider;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtTokenProvider {

    // 토큰 만료 시간
    private final int accessTokenExpirationMs;
    // 리프레시 토큰 만료 시간
    private final int refreshTokenExpirationMs;

    // 토큰을 서명하기 위한 서명자
    private final JWSSigner accessTokenSigner;
    // 토큰의 서명을 검증하기 위한 검증자
    private final JWSVerifier accessTokenVerifier;
    // 리프레시 토큰을 서명하기 위한 서명자
    private final JWSSigner refreshTokenSigner;
    // 리프레시 토큰의 서명을 검증하기 위한 검증자
    private final JWSVerifier refreshTokenVerifier;
    
    // 로그아웃된 access 토큰들을 저장하는 블랙리스트
    private final ConcurrentHashMap<String, Long> blacklistedAccessTokens = new ConcurrentHashMap<>();

    // 로그아웃된 refresh 토큰들을 저장하는 블랙리스트
    private final ConcurrentHashMap<String, Long> blacklistedRefreshTokens = new ConcurrentHashMap<>();

    public JwtTokenProvider(
            @Value("${jwt.access-token.secret}") String accessTokenSecret,
            @Value("${jwt.access-token.exp}") int accessTokenExpirationMs,
            @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${jwt.refresh-token.exp}") int refreshTokenExpirationMs
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

    public String generateAccessToken(UserDto userDto) throws JOSEException {

        log.info("[TokenProvider] generateAccessToken 호출됨 엑세스 토큰 생성 호출. 호출자 {}", userDto.username());

        // 생성할 토큰의 타입이 "access"인 경우 액세스 토큰을 생성한다.
        return generateToken(userDto, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(UserDto userDto) throws JOSEException {

        System.out.println("[TokenProvider] generateRefreshToken 호출됨: " + userDto.username() + "의 리프레시 토큰 생성");

        // 생성할 토큰의 타입이 "refresh"인 경우 리프레시 토큰을 생성한다.
        return generateToken(userDto, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    public boolean validateAccessToken(String accessToken) {
        log.info("[TokenProvider] 토큰 검증 호출 : {}", accessToken);

        // 블랙리스트에 있는 토큰인지 확인
        if (blacklistedAccessTokens.containsKey(accessToken)) {
            log.error("[TokenProvider] 블랙리스트에 있는 access 토큰입니다: {}", accessToken);
            return false;
        }

        return verifyToken(accessToken, accessTokenVerifier, "access");
    }

    public boolean validateRefreshToken(String refreshToken) {
        log.info("[TokenProvider] 리프레시 토큰 검증 호출 : {}", refreshToken);

        // 블랙리스트에 있는 토큰인지 확인
        if (blacklistedRefreshTokens.containsKey(refreshToken)) {
            log.error("[TokenProvider] 블랙리스트에 있는 access 토큰입니다: {}", refreshToken);
            return false;
        }

        return verifyToken(refreshToken, refreshTokenVerifier, "refresh");
    }

    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {

        try {
            // 토큰 파싱
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 검증
            if (!signedJWT.verify(verifier)) {
                log.error("[TokenProvider] 서명 검증 실패");
                return false;
            }

            // 토큰 타입 검증
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.error("[TokenProvider] verifyToken: 타입 불일치 - expected= {}, actual={}", expectedType, tokenType);
                return false;
            }

            // 만료 시간 검증
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            return exp != null && exp.after(new Date());
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateToken(UserDto userDto, int expirationMs, JWSSigner signer, String type ) throws JOSEException {

        // 토큰 식별자 생성
        String tokenId = UUID.randomUUID().toString();

        // 현재 시간을 기준으로 만료 시간 설정
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        // 토큰의 클레임 설정
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                // 토큰 주체(subject)
                .subject(userDto.username())
                // 토큰 식별자(jti)
                .jwtID(tokenId)
                // userId
                .claim("userId", userDto.id())
                // 토큰 타입
                .claim("type", type)
                // 사용자 권한
                .claim("roles",
                        List.of(userDto.role())
                )
                // 토큰 발급 시간(iat)
                .issueTime(now)
                .expirationTime(expiration)
                .build();

        // 토큰 생성 (아직 서명은 안됨)
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // 이제 토큰 서명
        signedJWT.sign(signer);

        // 토큰 직렬화 : JWT 토큰을 생성한 후 URL 안전한 문자열(Base64 인코딩)로 직렬화
        String completedJWT = signedJWT.serialize();

        log.info("[TokenProvider] 토큰 생성 완료 {}", completedJWT);
        return completedJWT;
    }

    public String getUsernameFromToken(String token) {
        try {
            log.info("[TokenProvider] getUsernameFromToken 호출됨: subject 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.info("[TokenProvider] getUsernameFromToken 결과: subject= {}", subject);

            return subject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public void expireRefreshCookie(HttpServletResponse response) {

        System.out.println("[TokenProvider] expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }
    
    // Access 토큰을 블랙리스트에 추가
    public void blacklistAccessToken(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            blacklistedAccessTokens.put(accessToken, System.currentTimeMillis());
            log.info("[TokenProvider] Access 토큰을 블랙리스트에 추가: {}", accessToken);
        }
    }

    // Access 토큰을 블랙리스트에 추가
    public void blacklistRefreshToken(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            blacklistedRefreshTokens.put(refreshToken, System.currentTimeMillis());
            log.info("[TokenProvider] Access 토큰을 블랙리스트에 추가: {}", refreshToken);
        }
    }
    
    // 만료된 토큰들을 정리 (선택적)
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime - (accessTokenExpirationMs); // access 토큰 만료 시간만큼 이전
        
        blacklistedAccessTokens.entrySet().removeIf(entry -> entry.getValue() < expirationTime);
        log.info("[TokenProvider] 만료된 블랙리스트 토큰 정리 완료");
    }

    public Cookie generateRefreshTokenExpirationCookie() {

        System.out.println("[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie("REFRESH_TOKEN", "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);

        System.out.println("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }
}
