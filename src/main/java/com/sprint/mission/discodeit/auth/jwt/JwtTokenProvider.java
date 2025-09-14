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

    // 액세스 토큰의 만료 시간(밀리초 단위)
    private final int accessTokenExpirationMs;
    // 리프레시 토큰의 만료 시간(밀리초 단위)
    private final int refreshTokenExpirationMs;

    // 액세스 토큰을 서명하기 위한 서명자
    private final JWSSigner accessTokenSigner;
    // 액세스 토큰의 서명을 검증하기 위한 검증자
    private final JWSVerifier accessTokenVerifier;
    // 리프레시 토큰을 서명하기 위한 서명자
    private final JWSSigner refreshTokenSigner;
    // 리프레시 토큰의 서명을 검증하기 위한 검증자
    private final JWSVerifier refreshTokenVerifier;

    /**
     * 구성 프로퍼티를 기반으로 토큰 서명/검증자와 만료 시간을 초기화한다.
     * 이 생성자는 애플리케이션 시작 시 한 번 호출되며, 이후 발급/검증 로직에서 재사용된다.
     *
     * @param accessTokenSecret Access 토큰에 사용할 HMAC 비밀키(HS256)
     * @param accessTokenExpirationMs Access 토큰 만료 시간(ms)
     * @param refreshTokenSecret Refresh 토큰에 사용할 HMAC 비밀키(HS256)
     * @param refreshTokenExpirationMs Refresh 토큰 만료 시간(ms)
     * @throws JOSEException 서명자/검증자 초기화 실패 시 발생
     */
    public JwtTokenProvider(
        // application.yaml 파일에 정의된 프로퍼티 값을 주입받는다.
        @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
        @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
        @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs
    ) throws JOSEException {

        log.info("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

        // 주입받은 만료 시간 값들을 필드에 저장하여 토큰 생성 시 사용할 수 있도록 설정한다.
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        // 액세스 토큰용 비밀키를 바이트 배열로 변환하여 HMAC-SHA256 서명자와 검증자를 생성한다.
        // 이를 통해 액세스 토큰의 무결성을 보장하고 위변조를 방지할 수 있다.
        // Access 토큰 검증/서명을 위한 비밀키 바이트 배열을 준비한다.
        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        // 리프레시 토큰용 비밀키를 바이트 배열로 변환하여 별도의 서명자와 검증자를 생성한다.
        // 액세스 토큰과 다른 비밀키를 사용함으로써 각 토큰의 독립적인 보안성을 확보한다.
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

        log.info("[TokenProvider] generateAccessToken 호출됨: {} 의 엑세스 토큰 생성 ", userDetails.getUsername());

        // 생성할 토큰의 타입이 "access"인 경우 액세스 토큰을 생성한다.
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

        log.info("[TokenProvider] generateRefreshToken 호출됨: {} 의 리프레시 토큰 생성 ", userDetails.getUsername());

        // 생성할 토큰의 타입이 "refresh"인 경우 리프레시 토큰을 생성한다.
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
    private String generateToken(DiscodeitUserDetails userDetails, int expirationMs, JWSSigner signer,
        String tokenType) throws JOSEException {

        log.info("[TokenProvider] generateToken: {}의 {} 토큰 생성 시작", userDetails.getUsername(), tokenType);

        // 토큰의 고유 식별자(jti)로 사용할 랜덤 UUID를 생성한다.
        String tokenId = UUID.randomUUID().toString();
        UserDto user = userDetails.getUserDto();

        // 현재 시간을 기준으로 토큰의 만료 시간을 설정한다.
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        // 토큰의 클레임(claims)을 설정한다.
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            // 토큰 주체(sub)
            .subject(user.username())
            // 토큰 고유 식별자(jti)
            .jwtID(tokenId)
            // 사용자 ID(userId)
            .claim("userId", user.id().toString())
            // 토큰 타입(type)
            .claim("type", tokenType)
            // 사용자 권한(roles)
            .claim("roles",
                userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
            )
            // 토큰 발급 시간(iat)
            .issueTime(now)
            // 토큰 만료 시간(exp)
            .expirationTime(expiryDate)
            .build();

        // 토큰 생성: 준비된 클레임과 헤더(HS256)를 사용하여 토큰 생성.
        // 단, 아직 서명되지 않은 토큰이다.
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // 토큰 서명: 생성된 토큰에 서명자를 적용하여 서명.
        signedJWT.sign(signer);

        // 토큰 직렬화: 실질적으로 JWT 토큰을 생성한 후 URL 안전한 문자열(Base64 인코딩)로 직렬화하는 메서드.
        String token = signedJWT.serialize();

        log.info("[TokenProvider] generateToken: {} 의 {} 토큰 생성 완료: {}", userDetails.getUsername(), tokenType, token);

        return token;
    }

    /**
     * 리프레시 토큰을 HttpOnly 쿠키로 생성한다.
     * 로그인 성공 또는 리프레시 성공 시 브라우저로 내려보낼 때 사용된다.
     *
     * @param refreshToken 직렬화된 JWT 문자열
     * @return HttpOnly 설정이 적용된 쿠키 인스턴스
     */
    public Cookie generateRefreshTokenCookie(String refreshToken) {

        log.info("[TokenProvider] generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);	// 개발 환경: HTTP도 동작하도록 Secure=false (운영 환경은 true를 사용해 HTTPS 통신을 이용할 수 있도록 권장)
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.info("[TokenProvider] generateRefreshTokenCookie 완료: Max-Age= {}", (refreshTokenExpirationMs / 1000));

        return cookie;
    }

    /**
     * 리프레시 토큰 쿠키를 즉시 만료시키는 쿠키를 생성한다.
     * 로그아웃이나 보안 이벤트 발생 시 클라이언트 보유 RT를 제거하기 위해 사용한다.
     *
     * @return Max-Age=0으로 설정된 만료 쿠키
     */
    public Cookie generateRefreshTokenExpirationCookie() {

        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);		// 쿠키 만료 시간을 0으로 설정하여 즉시 만료시킨다

        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }

    /**
     * 리프레시 토큰을 담은 HttpOnly 쿠키를 응답에 추가한다.
     */
    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        log.info("[TokenProvider] addRefreshCookie 호출됨: RT 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

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
            throw new IllegalArgumentException(e);
        }
    }

}