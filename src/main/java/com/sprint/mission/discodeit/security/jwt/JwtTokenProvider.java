package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰의 생성, 검증, 관리를 담당하는 컴포넌트입니다.
 * 
 * <p>Access Token과 Refresh Token을 생성하고, 토큰의 유효성을 검증하며,
 * 쿠키 관리를 담당합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Access Token 및 Refresh Token 생성</li>
 *   <li>토큰 유효성 검증 (서명, 만료, 타입)</li>
 *   <li>토큰에서 사용자 정보 추출</li>
 *   <li>리프레시 토큰 쿠키 관리</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
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

    /**
     * JwtTokenProvider를 생성합니다.
     * 
     * <p>설정 파일에서 JWT 시크릿 키와 만료 시간을 읽어와 
     * 토큰 서명자와 검증자를 초기화합니다.</p>
     * 
     * @param accessTokenSecret Access Token 서명용 시크릿 키
     * @param accessTokenExpirationMs Access Token 만료 시간 (밀리초)
     * @param refreshTokenSecret Refresh Token 서명용 시크릿 키
     * @param refreshTokenExpirationMs Refresh Token 만료 시간 (밀리초)
     * @throws JOSEException JWT 관련 예외
     */
    public JwtTokenProvider(
            @Value("${jwt.access-token.secret}") String accessTokenSecret,
            @Value("${jwt.access-token.exp}") int accessTokenExpirationMs,
            @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${jwt.refresh-token.exp}") int refreshTokenExpirationMs
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

    /**
     * 사용자 정보를 기반으로 Access Token을 생성합니다.
     * 
     * @param userDetails 사용자 상세 정보
     * @return 생성된 Access Token
     * @throws JOSEException JWT 생성 중 발생할 수 있는 예외
     */
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info(PROVIDER_NAME + "generateAccessToken 호출됨: {} 의 액세스 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    /**
     * 사용자 정보를 기반으로 Refresh Token을 생성합니다.
     * 
     * @param userDetails 사용자 상세 정보
     * @return 생성된 Refresh Token
     * @throws JOSEException JWT 생성 중 발생할 수 있는 예외
     */
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.info(PROVIDER_NAME + "generateRefreshToken 호출됨: {} 의 리프레시 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    /**
     * JWT 토큰을 생성하는 공통 메소드입니다.
     * 
     * <p>사용자 정보, 만료 시간, 서명자, 토큰 타입을 기반으로 
     * JWT 토큰을 생성합니다.</p>
     * 
     * @param userDetails 사용자 상세 정보
     * @param expirationMs 토큰 만료 시간 (밀리초)
     * @param signer 토큰 서명자
     * @param tokenType 토큰 타입 (access 또는 refresh)
     * @return 생성된 JWT 토큰
     * @throws JOSEException JWT 생성 중 발생할 수 있는 예외
     */
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

        response.addCookie(cookie);
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
}



