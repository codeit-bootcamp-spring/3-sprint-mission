package com.sprint.mission.discodeit.security;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰을 발급, 갱신, 유효성 검증하는 Provider 컴포넌트.
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>Access Token 및 Refresh Token 생성</li>
 *   <li>토큰 유효성 검증</li>
 *   <li>토큰에서 subject 추출</li>
 * </ul>
 *
 * <p>Spring Bean 초기화 시점에 Secret을 기반으로 {@link MACSigner}와 {@link MACVerifier}를 생성합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private MACSigner signer;
    private MACVerifier verifier;

    // JWT 서명 및 검증에 사용될 Secret Key
    @Value("${jwt.secret}")
    private String secret;

    // Access Token 만료 시간(ms)
    @Value("${jwt.access-token-validity-ms}")
    private long accessTokenValidityMs;

    // Refresh Token 만료 시간(ms)
    @Value("${jwt.refresh-token-validity-ms}")
    private long refreshTokenValidityMs;

    /**
     * Bean 초기화 시점에 Signer와 Verifier 생성.
     *
     * @throws IllegalArgumentException Secret 키 길이가 256비트(32바이트) 미만이면 예외 발생
     */
    @PostConstruct
    public void init() {
        try {
            byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
            this.signer = new MACSigner(secretBytes);
            this.verifier = new MACVerifier(secretBytes);
        } catch (JOSEException e) {
            throw new IllegalArgumentException(
                "JWT Secret 키 길이가 부족합니다. 최소 256비트(32바이트) 이상이어야 합니다.", e);
        }
    }

    /**
     * Access Token 발급
     *
     * @param subject 토큰에 담을 주체(subject, 보통 사용자 식별자)
     * @return 생성된 Access Token 문자열
     */
    public String createAccessToken(String subject) {
        return createToken(subject, accessTokenValidityMs);
    }

    /**
     * Refresh Token 발급
     *
     * @param subject 토큰에 담을 주체(subject)
     * @return 생성된 Refresh Token 문자열
     */
    public String createRefreshToken(String subject) {
        return createToken(subject, refreshTokenValidityMs);
    }

    /**
     * 토큰 생성 공통 로직
     *
     * @param subject 토큰에 담을 주체(subject)
     * @param validityMs 토큰 만료 시간(ms)
     * @return 생성된 JWT 문자열
     * @throws RuntimeException 토큰 생성 중 예외 발생 시
     */
    private String createToken(String subject, long validityMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        // JWT Claims 설정
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(subject)
            .issueTime(now)
            .expirationTime(expiry)
            .build();

        try {
            // JWT 생성 및 서명
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );
            // 서명
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패", e);
        }
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token 검증할 JWT 문자열
     * @return 유효하면 true, 서명 불일치 또는 만료 시 false
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean validSignature = signedJWT.verify(verifier);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return validSignature && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 subject 추출
     *
     * @param token JWT 문자열
     * @return 토큰에 저장된 subject
     * @throws RuntimeException 파싱 실패 시
     */
    public String getSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("JWT 파싱 실패", e);
        }
    }
}