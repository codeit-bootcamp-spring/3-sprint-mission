package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-in-seconds:3600}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds:86400}")
    private long refreshTokenValidityInSeconds;

    /**
     * 액세스 토큰 생성
     */
    public String createAccessToken(String username) {
        return createToken(username, accessTokenValidityInSeconds);
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String username) {
        return createToken(username, refreshTokenValidityInSeconds);
    }

    /**
     * JWT 토큰 생성
     */
    private String createToken(String username, long validityInSeconds) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(username)
            .issueTime(new Date())
            .expirationTime(new Date(System.currentTimeMillis() + validityInSeconds * 1000))
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            JWSSigner signer = new MACSigner(secretKey.getBytes());
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("토큰 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("토큰에서 사용자명을 추출하는 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 토큰의 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

            // 서명 검증
            boolean isSignatureValid = signedJWT.verify(verifier);

            // 만료 시간 검증
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean isTokenExpired = expirationTime != null && expirationTime.before(new Date());

            return isSignatureValid && !isTokenExpired;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰으로부터 인증 정보 생성
     */
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰 갱신
     */
    public String refreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = getUsername(refreshToken);
        return createAccessToken(username);
    }
}