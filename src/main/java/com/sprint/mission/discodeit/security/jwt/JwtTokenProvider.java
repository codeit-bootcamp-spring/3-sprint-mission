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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH-TOKEN";

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(
            @Value("${jwt.access-token.secret}") String accessTokenSecret,
            @Value("${jwt.access-token.exp}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${jwt.refresh-token.exp}") long refreshTokenExpirationMs
    ) throws JOSEException {

        byte[] a = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        byte[] r = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        if (a.length < 32 || r.length < 32) {
            throw new IllegalStateException("JWT HS256 secrets must be at least 32 bytes.");
        }

        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        this.accessTokenSigner = new MACSigner(a);
        this.accessTokenVerifier = new MACVerifier(a);
        this.refreshTokenSigner = new MACSigner(r);
        this.refreshTokenVerifier = new MACVerifier(r);
    }

    /* ===== 발급 ===== */
    public String generateAccessToken(DiscodeitUserDetails user) throws JOSEException {
        return generateToken(user, accessTokenExpirationMs, accessTokenSigner, "access");
    }

    public String generateRefreshToken(DiscodeitUserDetails user) throws JOSEException {
        return generateToken(user, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
    }

    private String generateToken(DiscodeitUserDetails user, long expMs, JWSSigner signer,
                                 String type)
            throws JOSEException {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expMs);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())                      // username
                .jwtID(UUID.randomUUID().toString())              // jti
                .claim("uid", user.getUserDto().id().toString())  // 사용자 UUID
                .claim("roles", roles)
                .claim("type", type)                              // access | refresh
                .issueTime(now)
                .expirationTime(expiry)
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(signer);
        return jwt.serialize();
    }

    /* ===== 검증 ===== */
    public boolean validateAccessToken(String token) {
        return verify(token, accessTokenVerifier, "access");
    }

    public boolean validateRefreshToken(String token) {
        return verify(token, refreshTokenVerifier, "refresh");
    }

    private boolean verify(String token, JWSVerifier verifier, String expectedType) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier)) {
                return false;
            }

            String type = (String) jwt.getJWTClaimsSet().getClaim("type");
            if (!expectedType.equals(type)) {
                return false;
            }

            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            log.debug("JWT verify failed: {}", e.getMessage());
            return false;
        }
    }

    /* ===== 파싱 헬퍼 ===== */
    public String getUsername(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String getTokenId(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            return null;
        }
    }

    public Cookie buildRefreshCookie(String refreshToken) {
        Cookie c = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        c.setHttpOnly(true);
        c.setSecure(false);
        c.setPath("/");
        c.setMaxAge((int) (refreshTokenExpirationMs / 1000));
        return c;
    }

    public Cookie buildExpireRefreshCookie() {
        Cookie c = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        c.setHttpOnly(true);
        c.setSecure(false);
        c.setPath("/");
        c.setMaxAge(0);
        return c;
    }

    public void addRefreshCookie(HttpServletResponse res, String refreshToken) {
        res.addCookie(buildRefreshCookie(refreshToken));
    }

    public void expireRefreshCookie(HttpServletResponse res) {
        res.addCookie(buildExpireRefreshCookie());
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie c : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}