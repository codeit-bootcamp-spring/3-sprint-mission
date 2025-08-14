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
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Access/Refresh 토큰 생성 및 검증을 담당하는 Bean
 */
@Slf4j
@Component
public class JwtTokenProvider {

    // Refresh 토큰을 저장할 HTTP 쿠키의 이름
    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH-TOKEN";

    // Access 토큰의 만료 시간(ms 단위)
    private final int accessTokenExpirationMs;
    // Refresh 토큰의 만료 시간(ms 단위)
    private final int refreshTokenExpirationMs;

    // Access 토큰을 서명하기 위한 서명자
    private final JWSSigner accessTokenSigner;
    // Access 토큰 서명을 검증하기 위한 검증자
    private final JWSVerifier accessTokenVerifier;

    // Refresh 토큰을 서명하기 위한 서명자
    private final JWSSigner refreshTokenSigner;
    // Refresh 토큰 서명을 검증하기 위한 검증자
    private final JWSVerifier refreshTokenVerifier;

    /**
     * 구성 프로퍼티를 기반으로 토큰 서명/검증자와 만료 시간을 초기화한다.
     *
     * @param accessTokenSecret        Access 토큰에 사용할 HMAC 비밀키(HS256)
     * @param accessTokenExpirationMs  Access 토큰 만료 시간
     * @param refreshTokenSecret       Refresh 토큰에 사용할 HMAC 비밀키(HS256)
     * @param refreshTokenExpirationMs Refresh 토큰 만료 시간
     * @throws JOSEException 서명자/검증자 초기화 실패 시 발생
     */
    public JwtTokenProvider(
        @Value("${jwt.access-token.secret}") String accessTokenSecret,
        @Value("${jwt.access-token.exp}") int accessTokenExpirationMs,
        @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${jwt.refresh-token.exp}") int refreshTokenExpirationMs
    ) throws JOSEException {

        log.debug("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        // Access 토큰용 비밀키를 byte 배열로 변환하여 HMAC-SHA256 서명자와 검증자를 생성한다.
        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        // Refresh 토큰용 비밀키를 byte 배열로 변환하여 HMAC-SHA256 서명자와 검증자를 생성한다.
        byte[] refreshTokenBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshTokenBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshTokenBytes);
    }

    /**
     * Access 토큰 생성
     *
     * @param userDetails 사용자 정보
     * @return 직렬화된 JWT 문자열(Access Token)
     * @throws JOSEException 토큰 서명과정에서 실패 시 발생
     */
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.debug("[TokenProvider] 유저: {}의 Access 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    /**
     * Refresh 토큰 생성
     *
     * @param userDetails 사용자 정보
     * @return 직렬화된 JWT 문자열(Refresh Token)
     * @throws JOSEException 토큰 서명과정에서 실패 시 발생
     */
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {

        log.debug("[TokenProvider] 유저: {}의 Refresh 토큰 생성", userDetails.getUsername());

        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    /**
     * 토큰 생성
     *
     * @param userDetails  사용자 정보
     * @param expirationMs 토큰 만료 시간
     * @param signer       토큰 서명자
     * @param tokenType    토큰 타입("access" 또는 "refresh")
     * @return 생성된 토큰
     * @throws JOSEException 토큰 생성 중 발생할 수 있는 예외
     */
    private String generateToken(DiscodeitUserDetails userDetails, int expirationMs,
        JWSSigner signer,
        String tokenType) throws JOSEException {

        String tokenId = UUID.randomUUID().toString();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userDetails.getUsername())
            .jwtID(tokenId)
            .claim("userId", userDetails.getUserResponseDto().id())
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

        // 토큰 생성: 준비된 클레임과 헤더를 사용하여 토큰 생성
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // 토큰 서명
        signedJWT.sign(signer);

        String completedJWT = signedJWT.serialize();

        log.debug("[TokenProvider] generateToken: {}의 {} 토큰 생성 완료: {}",
            userDetails.getUsername(), tokenType, completedJWT);

        return completedJWT;
    }

    /**
     * 리프레시 토큰을 HttpOnly 쿠키로 생성한다. 로그인 성공 또는 리프레시 성공 시 브라우저로 내려보낼 때 사용된다.
     *
     * @param refreshToken 직렬화된 JWT 문자열
     * @return HttpOnly 설정이 적용된 쿠키 인스턴스
     */
    public Cookie generateRefreshTokenCookie(String refreshToken) {

        log.debug("[TokenProvider] generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTP도 동작하도록 설정
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.debug("[TokenProvider] generateRefreshTokenCookie 완료: MAX-AGE = {}",
            refreshTokenExpirationMs / 1000);

        return cookie;
    }

    /**
     * Refresh 토큰 쿠키를 즉시 만료시키는 쿠키를 생성 로그아웃이나 보안 이벤트 발생 시 클라이언트 보유 Refresh Token을 제거하기 위해 사용한다.
     *
     * @return MAX-AGE=0으로 설정된 쿠키 (만료)
     */
    public Cookie generateRefreshTokenExpirationCookie() {

        log.debug(
            "[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTP도 동작하도록 설정
        cookie.setPath("/");
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 만료

        log.debug("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }

    /**
     * Refresh 토큰을 담은 HttpOnly 쿠키를 응답에 추가한다.
     */
    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        log.debug("[TokenProvider] addRefreshCookie 호출됨: Refresh Token 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    /**
     * 만료용 Refresh 쿠키를 응답에 추가
     */
    public void expireRefreshCookie(HttpServletResponse response) {
        log.debug("[TokenProvider] expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }

    /**
     * Access 토큰을 검증한다. 보호된 API에 대한 요청 처리 직전에 호출되며, 서명 무결성, 토큰 타입, 만료 여부를 순차적으로 검사한다.
     *
     * @param token 유효성 검사 대상 JWT 문자열
     */
    public boolean validateAccessToken(String token) {

        log.debug("[TokenProvider] validateAccessToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, accessTokenVerifier, "access");

        log.debug("[TokenProvider] validateAccessToken 결과: {}", result);

        return result;
    }

    /**
     * Refresh 토큰을 검증한다. 서명 무결성, 토큰 타입, 만료 여부를 확인하여 재발급 가능 여부를 결정
     *
     * @param token 유효성 검사 대상 JWT 문자열
     */
    public boolean validateRefreshToken(String token) {

        log.debug("[TokenProvider] validateRefreshToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, refreshTokenVerifier, "refresh");

        log.debug("[TokenProvider] validateRefreshToken 결과: {}", result);

        return result;
    }

    /**
     * 토큰의 서명과 클레임을 실제로 검증하는 메서드
     *
     * @param token        유효성 검사 대상 JWT 문자열
     * @param verifier     서명 검증자
     * @param expectedType 기대하는 토큰 타입
     */
    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {

        try {
            // Token 파싱
            log.debug("[TokenProvider] verifyToken: Token 파싱 시작");
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 무결성 검증
            log.debug("[TokenProvider] verityToken: 서명 무결성 검증 시작");
            if (!signedJWT.verify(verifier)) {
                log.debug("[TokenProvider] verityToken: 서명 무결성 검증 실패");
                return false;
            }

            log.debug("[TokenProvider] verityToken: 토큰 타입 검증 시작");
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.debug("[TokenProvider] Token type 불일치 expectedType: {}, actual: {}",
                    expectedType,
                    tokenType);
                return false;
            }

            log.debug("[TokenProvider] verityToken: 만료 시간 검증 시작");
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            // null이 아니고 현재 시간보다 이후인 경우 유효
            boolean valid = exp != null && exp.after(new Date());

            log.debug("[TokenProvider] 만료 검사 결과: {}", valid);

            return valid;
        } catch (Exception e) {
            log.warn("[TokenProvider] verifyToken 예외 발생: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 subject로 저장된 사용자명 추출
     *
     * @param token JWT 문자열
     * @return 사용자명(subject)
     */
    public String getUsernameFromToken(String token) {

        try {
            log.debug("[TokenProvider] getUsernameFromToken: subject 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.debug("[TokenProvider] getUsernameFromToken 결과 subject: {}", subject);

            return subject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token", e);
        }
    }

    /**
     * 토큰에서 JWT ID(jti) 추출
     *
     * @param token JWT 문자열
     * @return jti 값
     */
    public String getTokenId(String token) {

        try {
            log.debug("[TokenProvider] getTokenId: jti 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            log.debug("[TokenProvider] getTokenId 결과 jti: {}", jti);

            return jti;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token", e);
        }
    }

    /**
     * 토큰에서 발급 시간(iat) 추출
     *
     * @param token JWT 문자열
     * @return 발급 시간(Date)
     */
    public Date getIssuedAt(String token) {

        try {
            log.debug("[TokenProvider] getIssuedAt: iat 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date iat = signedJWT.getJWTClaimsSet().getIssueTime();

            log.debug("[TokenProvider] getIssuedAt 결과 iat: {}", iat);

            return iat;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token", e);
        }
    }

    /**
     * 토큰에서 만료 시간(exp) 추출
     *
     * @param token JWT 문자열
     * @return 만료 시간(Date)
     */
    public Date getExpiration(String token) {

        try {
            log.debug("[TokenProvider] getExpiration: exp 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            log.debug("[TokenProvider] getExpiration 결과 exp: {}", exp);

            return exp;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token", e);
        }
    }
}
