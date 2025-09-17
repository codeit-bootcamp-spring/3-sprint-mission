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
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    // 토큰 서명/검증자와 만료 시간을 초기화
    // 애플리케이션 시작 시 한 번 호출되고, 이후 발급/검증 로직에서 재사용 됨
    public JwtTokenProvider(
        // application.yaml 파일에 정의된 프로퍼티 값 주입
        @Value("${jwt.access-token.secret}") String accessTokenSecret,
        @Value("${jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
        @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
        @Value("${jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs
    ) throws JOSEException {

        log.info("[TokenProvider] 생성자 호출됨: 토큰 서명/검증자 및 만료 시간 초기화");

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        // 리프레시 토큰용 비밀키를 바이트 배열로 변환하여 별도의 서명자와 검증자를 생성한다.
        // 액세스 토큰과 다른 비밀키를 사용함으로써 각 토큰의 독립적인 보안성을 확보한다.
        byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

        byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    }

    /**
     * 액세스 토큰 생성 로그인 성공 또는 리프레시 토큰을 통한 재발급 시 호출 단기 인증에 사용되는 짧은 수명의 토큰 발급
     */
    public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.info("[TokenProvider] generateAccessToken 호출됨: {}의 액세스 토큰 생성",
            userDetails.getUsername());
        return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    /**
     * 리프레시 토큰 생성 로그인 성공 또는 리프레시 시 토큰 회전 정책에 따라 새 RT를 발급할 때 호출 쿠키에 저장되어 액세스 토큰 재발급 시도에 사용
     */
    public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
        log.info("[TokenProvider] generateRefreshToken 호출됨: {}의 리프레시 토큰 생성",
            userDetails.getUsername());
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
        JWSSigner signer, String tokenType) throws JOSEException {
        log.info("[TokenProvider] generateToken: {}의 {} 토큰 생성 시작", userDetails.getUsername(),
            tokenType);

        String tokenId = UUID.randomUUID().toString();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userDetails.getUserId().toString())
            .jwtID(tokenId)
            .claim("username", userDetails.getUsername())
            .claim("email", userDetails.getUserDto().email())
            .claim("role", userDetails.getUserDto().role().name())
            .claim("type", tokenType)
            .issueTime(now)
            .expirationTime(expiryDate)
            .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // 토큰 서명: 생성된 토큰에 서명자 적용하여 서명
        signedJWT.sign(signer);

        // 토큰 직렬화: 실질적으로 JWT 토큰을 생성한 후 URL 안전한 문자열(Base64 인코딩)로 직렬화
        String completedJWT = signedJWT.serialize();
        log.info("[TokenProvider] generateToken: {}의 {} 토큰 생성 완료", userDetails.getUsername(),
            tokenType);

        return completedJWT;
    }

    /**
     * 리프레시 토큰을 HttpOnly 쿠키로 생성 로그인 성공 또는 리프레시 성공 시 브라우저로 내려보낼 때 사용
     *
     * @param refreshToken 직렬화된 JWT 문자열
     * @return HttpOnly 설정이 적용된 쿠키 인스턴스
     */
    public Cookie generateRefreshTokenCookie(String refreshToken) {
        log.info("[TokenProvider] generateRefreshTokenCookie 호출됨: Refresh Token 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(refreshTokenExpirationMs / 1000);

        log.info("[TokenProvider] generateRefreshTokenCookie 완료: Max-Age={}",
            (refreshTokenExpirationMs / 1000));
        return cookie;
    }

    /**
     * 리프레시 토큰 쿠키를 즉시 만료시키는 쿠키를 생성한다. 로그아웃이나 보안 이벤트 발생 시 클라이언트 보유 RT를 제거하기 위해 사용한다.
     *
     * @return Max-Age=0으로 설정된 만료 쿠키
     */
    public Cookie generateRefreshTokenExpirationCookie() {
        log.info(
            "[TokenProvider] generateRefreshTokenExpirationCookie 호출됨: Refresh Token 만료 쿠키 생성");

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);    // 쿠키 만료 시간을 0으로 설정하여 즉시 만료시킨다
        log.info("[TokenProvider] generateRefreshTokenExpirationCookie 완료");

        return cookie;
    }

    /**
     * 리프레시 토큰을 담은 HttpOnly 쿠키를 응답에 추가
     */
    public void addRefreshCookie(HttpServletResponse response, String refreshToken) {
        log.info("[TokenProvider] addRefreshCookie 호출됨: RT 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenCookie(refreshToken);

        response.addCookie(cookie);
    }

    /**
     * 만료(삭제)용 리프레시 쿠키를 응답에 추가 재사용 차단이나 강제 로그아웃 시 사용
     */
    public void expireRefreshCookie(HttpServletResponse response) {
        log.info("[TokenProvider] expireRefreshCookie 호출됨: 만료 쿠키 응답에 추가");
        Cookie cookie = generateRefreshTokenExpirationCookie();

        response.addCookie(cookie);
    }

    /**
     * 액세스 토큰을 검증 보호된 API에 대한 요청 처리 직전에 호출 서명 무결성, 토큰 타입, 만료 여부를 순차적으로 검사
     *
     * @param token 검사 대상 JWT 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateAccessToken(String token) {
        log.info("[TokenProvider] validateAccessToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, accessTokenVerifier, "access");
        log.info("[TokenProvider] validateAccessToken 결과: {}", result);

        return result;
    }

    /**
     * 리프레시 토큰 검증 'api/auth/refresh' 호풀 시 쿠키에서 읽어온 토큰을 대상으로 사용 서묭 무결성, 토큰 타입, 만료 여부를 확인하여 재발급 가능 여부
     * 결정
     *
     * @param token 검사 대상 JWT 문자열(쿠키에서 추출됨)
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateRefreshToken(String token) {
        log.info("[TokenProvider] validateRefreshToken 호출됨: 토큰 유효성 검사 시작");

        boolean result = verifyToken(token, refreshTokenVerifier, "refresh");
        log.info("[TokenProvider] validateRefreshToken 결과: {}", result);

        return result;
    }

    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType) {

        try {
            log.info("[TokenProvider] verifyToken: 토큰 파싱 시작");
            SignedJWT signedJWT = SignedJWT.parse(token);

            log.info("[TokenProvider] verifyToken: 서명 무결성 검증 시작");
            if (!signedJWT.verify(verifier)) {
                log.warn("[TokenProvider] verifyToken: 서명 검증 실패");
                return false;
            }

            log.info("[TokenProvider] verifyToken: 토큰 타입 검증 시작");
            String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(tokenType)) {
                log.warn("[TokenProvider] verifyToken: 타입 불일치 - expected={}, actual={}",
                    expectedType, tokenType);
                return false;
            }

            log.info("[TokenProvider] verifyToken: 만료 시간 검증 시작");
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 만료 시간이 null이 아니고, 현재 시간보다 이후인 경우 유효(true)
            boolean valid = exp != null && exp.after(new Date());

            if (!valid) {
                log.warn("[TokenProvider] verifyToken: 토큰 만료됨 - exp={}, now={}", exp, new Date());
            } else {
                log.info("[TokenProvider] verifyToken: 토큰 유효함 - exp={}, now={}", exp, new Date());
            }

            return valid;
        } catch (Exception e) {
            log.warn("[TokenProvider] verifyToken: 예외 발생 - {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 토큰에서 주체(subject)로 저장된 사용자명을 추출 인증 필터가 사용자 정보를 로드하기 위해 호출
     */
    public String getUsernameFromToken(String token) {
        try {
            log.info("[TokenProvider] getUsernameFromToken 호출됨: subject 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();

            log.info("[TokenProvider] getUsernameFromToken 결과: subject={}", subject);

            return subject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * 토큰에서 userId(UUID)를 추출
     */
    public UUID getUserIdFromToken(String token) {
        try {
            log.info("[TokenProvider] getUserIdFromToken 호출됨: userId 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            return UUID.fromString(subject);

        } catch (Exception e) {
            log.warn("[TokenProvider] getUserIdFromToken 실패: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * 토큰에서 JWT ID(jti)를 추출 토큰 상태 저장소(JwtSessionRegistry)에서 폐기 여부를 판단할 때 사용
     */
    public String getTokenId(String token) {
        try {
            log.info("[TokenProvider] getTokenId 호출됨: jti 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            log.info("[TokenProvider] getTokenId 결과: jti={}", jti);

            return jti;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * 토큰에서 발급 시간(iat)를 추출 디버깅이나 감사 로그에서 토큰 생성 시점을 확인할 때 유용
     */
    public Date getIssuedAt(String token) {
        try {
            log.info("[TokenProvider] getIssuedAt 호출됨: iat 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date iat = signedJWT.getJWTClaimsSet().getIssueTime();

            log.info("[TokenProvider] getIssuedAt 호출됨: iat={}", iat);

            return iat;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    /**
     * 토큰에서 만료 시간(exp)를 추출 남은 유효 시간을 계산하거나 만료 임박 알림을 구현할 때 사용
     */
    public Date getExpiration(String token) {
        try {
            log.info("[TokenProvider] getExpiration 호출됨: exp 추출 시작");

            SignedJWT signedJWT = SignedJWT.parse(token);
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();

            log.info("[TokenProvider] getExpiration 호출됨: exp={}", exp);

            return exp;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
}
