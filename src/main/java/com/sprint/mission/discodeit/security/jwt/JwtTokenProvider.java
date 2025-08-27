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
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String jwtSecret;
    private final int jwtExpirationInMinutes;
    private final int refreshTokenExpirationInDays;

    public JwtTokenProvider(
        @Value("${jwt.secret:defaultSecretKeyForJWTWhichShouldBeChangedInProduction}") String jwtSecret,
        @Value("${jwt.expiration-minutes:60}") int jwtExpirationInMinutes,
        @Value("${jwt.refresh-token-expiration-days:7}") int refreshTokenExpirationInDays) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMinutes = jwtExpirationInMinutes;
        this.refreshTokenExpirationInDays = refreshTokenExpirationInDays;
    }

    /**
     * JWT 액세스 토큰 생성
     * */
    public String generateAccessToken(Authentication authentication) {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationInMinutes, ChronoUnit.MINUTES);

        try {
            // JWT Claim 설정
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiration))
                .claim("userId",userDetails.getUserDto().id().toString())
                .claim("email",userDetails.getUserDto().email())
                .claim("roles",userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()))
                .claim("type","access")
                .build();

            // JWT 서명
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );

            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("JWT 액세스 토큰 생성 중 오류 발생",e);
            throw new RuntimeException("JWT 생성에 실패 했습니다.", e);
        }
    }

    /**
     * JWT 리프레시 토큰 생성
     * */
    public String generateRefreshToken(Authentication authentication) {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expirationTime = now.plus(refreshTokenExpirationInDays, ChronoUnit.DAYS);

        try {
            // JWT Claims 설정 - 리프레시 토큰은 최소 정보만 포함
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expirationTime))
                .claim("userId", userDetails.getUserDto().id().toString())
                .claim("type", "refresh")
                .build();

            // JWT 서명
            SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
            );

            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException e) {
            log.error("JWT 리프레시 토큰 생성 중 오류 발생", e);
            throw new RuntimeException("JWT 생성에 실패했습니다.", e);
        }
    }

    /**
     * JWT 토큰에서 사용자명 추출
     */
    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            log.error("JWT 토큰에서 사용자명 추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID 추출 ( UUID 형태로 반환 )
     * */
    public UUID extractUserId(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String userIdStr = signedJWT.getJWTClaimsSet().getStringClaim("userId");
            return userIdStr != null ? UUID.fromString(userIdStr) : null;
        } catch (Exception e) {
            log.error("JWT에서 사용자 ID 추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * JWT 토큰에서 이메일 추출
     */
    public String extractEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getStringClaim("email");
        } catch (Exception e) {
            log.error("JWT에서 이메일 추출 중 오류 발생", e);
            return null;
        }
    }

    /**
     * JWT 토큰에서 권한 목록 추출
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractRoles(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return (java.util.List<String>) signedJWT.getJWTClaimsSet().getClaim("roles");
        } catch (Exception e) {
            log.error("JWT에서 권한 추출 중 오류 발생", e);
            return java.util.List.of();
        }
    }

    /**
     * JWT 토큰 유효성 검사
     * */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 서명 검증
            JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
            if (!signedJWT.verify(verifier)) {
                log.warn("JWT 서명이 유효하지 않습니다.");
                return false;
            }

            // 만료시간 검증
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                log.warn("JWT 토큰이 만료되었습니다.");
                return false;
            }

            return true;
        } catch (ParseException | JOSEException e) {
            log.error("JWT 토큰 유효성 검사 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 토큰 타입 확인 ( access 또는 refresh )
     * */
    public String getTokenType(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getStringClaim("type");
        } catch (ParseException e) {
            log.error("JWT 타입 확인 중 오류 발생",e);
            return null;
        }
    }

     /**
      * 토큰의 남은 유효시간( 초 ) 반환
      * */
     public long getTokenExpirationTime(String token) {
         try {
             SignedJWT signedJWT = SignedJWT.parse(token);
             Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
             if (expirationTime != null) {
                 return (expirationTime.getTime() - System.currentTimeMillis()) / 1000;
             }
             return 0;
         } catch (ParseException e) {
             log.error("JWT 만료 시간 확인 중 오류 발생", e);
             return 0;
         }
     }

     /**
      * 토큰의 만료 날짜 반환
      * */
     public Date getExpirationDate(String token) {
         try {
             SignedJWT signedJWT = SignedJWT.parse(token);
             return signedJWT.getJWTClaimsSet().getExpirationTime();
         } catch (ParseException e) {
             log.error("JWT 토큰 만료 날짜 확인 중 오류 발생", e);
             return null;
         }
     }

    /**
     * 리프레시 토큰을 이용하여 새로운 액세스 토큰 생성
     */
    public String refreshAccessToken(String refreshToken, Authentication authentication) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        String tokenType = getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("리프레시 토큰이 아닙니다.");
        }

        return generateAccessToken(authentication);
    }

    /**
     * 토큰이 만료되었는지 확인
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate != null && expirationDate.before(new Date());
    }

    /**
     * 액세스 토큰 유효시간 반환 (초 단위)
     */
    public long getAccessTokenValidityInSeconds() {
        return jwtExpirationInMinutes * 60L;
    }

    /**
     * 리프레시 토큰 유효시간 반환 (초 단위)
     */
    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenExpirationInDays * 24L * 60L * 60L;
    }

}
