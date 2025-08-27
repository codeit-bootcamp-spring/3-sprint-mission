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

    /**
     * 토큰 서명/검증자와 만료 시간을 초기화한다.
     * 이 생성자는 애플리케이션 시작 시 한 번 호출되며, 이후 발급/검증 로직에서 재사용된다.
     *
     * @param accessTokenSecret Access 토큰에 사용할 HMAC 비밀키(HS256)
     * @param accessTokenExpirationMs Access 토큰 만료 시간(ms)
     * @param refreshTokenSecret Refresh 토큰에 사용할 HMAC 비밀키(HS256)
     * @param refreshTokenExpirationMs Refresh 토큰 만료 시간(ms)
     * @throws JOSEException 서명자/검증자 초기화 실패 시 발생
     */
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

    /**
     * 액세스 토큰을 생성한다.
     * 로그인 성공 또는 리프레시 토큰을 통한 재발급 시 호출되며,
     * 단기 인증에 사용되는 짧은 수명의 토큰을 발급한다.
     *
     * @param userDetails 사용자 정보(아이디, 권한, 내부 식별자)
     * @return 직렬화된 JWT 문자열(Access Token)
     * @throws JOSEException 토큰 서명 과정에서 실패할 경우 발생한다.
     */
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.info("[TokenProvider] generateAccessToken 호출: " + userDetails.getUsername() + "의 엑세스 토큰 생성");

        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    /**
     * 리프레시 토큰을 생성한다.
     * 로그인 성공 또는 리프레시 시 토큰 회전(rotation) 정책에 따라 새 RT를 발급할 때 호출된다.
     * 이 토큰은 쿠키(HttpOnly)에 저장되어 액세스 토큰 재발급 시도에 사용된다.
     *
     * @param userDetails 사용자 정보(아이디, 권한, 내부 식별자)
     * @return 직렬화된 JWT 문자열(Refresh Token)
     * @throws JOSEException 토큰 서명 과정에서 실패할 경우 발생한다.
     */
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.info("[TokenProvider] generateRefreshToken 호출: " + userDetails.getUsername() + "의 리프레시 토큰 생성");

        return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    /**
     * 토큰 생성
     * @param userDetails 사용자 정보
     * @param expirationMs 토큰 만료 시간
     * @param signer 토큰 서명자
     * @param tokenType 토큰 타입("access" 또는 "refresh")
     * @return 생성된 토큰
     * @throws JOSEException 토큰 생성 중 발생할 수 있는 예외
     */
    private String generateToken(
        DiscodeitUserDetails userDetails,
        int expirationMs,
        JWSSigner signer,
        String tokenType
    ) throws JOSEException {
        log.info("[TokenProvider] generateToken 호출: " + userDetails.getUsername() + "의 " + tokenType + " 토큰 생성");

        String tokenId = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

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
            .expirationTime(expiryDate)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        String completedJWT = signedJWT.serialize();

        log.debug("[TokenProvider] generateToken: " + userDetails.getUsername() + "의 " + tokenType + " 토큰 생성 완료: " + completedJWT);

        return completedJWT;
    }

    /**
     * 리프레시 토큰을 HttpOnly 쿠키로 생성한다.
     * 로그인 성공 또는 리프레시 성공 시 브라우저로 내려보낼 때 사용된다.
     *
     * @param refreshToken 직렬화된 JWT 문자열
     * @return HttpOnly 설정이 적용된 쿠키 인스턴스
     */
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        log.info("[TokenProvider] generateRefreshTokenCookie 호출: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.debug("[TokenProvider] generateRefreshTokenCookie 완료: Max-Age=" + (refreshTokenExpirationMs / 1000));

        return cookie;
    }

    /**
     * 리프레시 토큰을 담은 HttpOnly 쿠키를 응답에 추가한다.
     */
    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        log.info("[TokenProvider] addRefreshCookie 호출: RT 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    /**
     * 리프레시 토큰 쿠키를 즉시 만료시키는 쿠키를 생성한다.
     * 로그아웃이나 보안 이벤트 발생 시 클라이언트 보유 RT를 제거하기 위해 사용한다.
     *
     * @return Max-Age=0으로 설정된 만료 쿠키
     */
    public Cookie generateRefreshTokenExpirationCookie() {
        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 호출: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        log.debug("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }

    /**
     * 만료(삭제)용 리프레시 쿠키를 응답에 추가한다.
     * 재사용 차단이나 강제 로그아웃 시 사용한다.
     */
    public void expireRefreshCookie(HttpServletResponse response) {
        log.info("[TokenProvider] expireRefreshCookie 호출: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }

    /**
     * 액세스 토큰을 검증한다.
     * 보호된 API에 대한 요청 처리 직전에 호출되며,
     * 서명 무결성, 토큰 타입, 만료 여부를 순차적으로 검사한다.
     *
     * @param token 검사 대상 JWT 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateAccessToken(String token) {
        log.info("[TokenProvider] validateAccessToken 호출: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, accessTokenVerifier, "access");
        log.debug("[TokenProvider] validateAccessToken 결과: " + result);

        return result;
    }

    /**
     * 리프레시 토큰을 검증한다.
     * `/api/auth/refresh` 호출 시 쿠키에서 읽어온 토큰을 대상으로 사용된다.
     * 서명 무결성, 토큰 타입, 만료 여부를 확인하여 재발급 가능 여부를 결정한다.
     *
     * @param token 검사 대상 JWT 문자열(쿠키에서 추출됨)
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateRefreshToken(String token) {
        log.info("[TokenProvider] validateRefreshToken 호출: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, refreshTokenVerifier, "refresh");
        log.debug("[TokenProvider] validateRefreshToken 결과: " + result);

        return result;
    }

    /**
     * 토큰의 서명과 클레임을 실제로 검증하는 내부 유틸리티 메서드이다.
     * 먼저 토큰을 파싱한 뒤, 제공된 검증자(HMAC)로 서명 무결성을 확인한다.
     * 이어서 `type` 클레임이 기대한 값과 일치하는지 검사하고, 마지막으로 만료 시간을 판정한다.
     *
     * @param token 검사 대상 JWT 문자열
     * @param verifier 서명 검증에 사용할 검증자(Access/Refresh별로 구분)
     * @param expectedType 기대하는 토큰 타입("access" 또는 "refresh")
     * @return 모든 조건을 충족하면 true, 하나라도 실패하면 false
     */
    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {
        log.info("[TokenProvider] verifyToken 호출: 토큰 검증 시작");

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                log.warn("[TokenProvider] verifyToken: 서명 검증 실패");
                return false;
            }

            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.debug("[TokenProvider] verifyToken: 타입 불일치 - expected=" + expectedType + ", actual=" + tokenType);
                return false;
            }

            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean valid = exp != null && exp.after(new Date());

            log.debug("[TokenProvider] verifyToken: 만료 검증 결과=" + valid);

            return valid;
        } catch (Exception e) {
            log.error("[TokenProvider] verifyToken: 예외 발생 - " + e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 주체(subject)로 저장된 사용자명을 추출한다.
     * 인증 필터가 사용자 정보를 로드하기 위해 호출한다.
     *
     * @param token JWT 문자열
     * @return 사용자명
     */
    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰", e);
        }
    }

    /**
     * 토큰에서 주체(subject)로 저장된 사용자 Id를 추출한다.
     * 인증 필터가 사용자 정보를 로드하기 위해 호출한다.
     *
     * @param token JWT 문자열
     * @return 사용자 Id
     */
    public UUID getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            return UUID.fromString((String) signedJWT.getJWTClaimsSet().getClaim("userId"));
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰", e);
        }
    }
}