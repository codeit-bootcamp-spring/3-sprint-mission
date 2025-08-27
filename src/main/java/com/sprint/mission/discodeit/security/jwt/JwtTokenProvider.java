package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;
    private final JWSSigner accessTokenSigner;
    private final JWSVerifier accessTokenVerifier;
    private final JWSSigner refreshTokenSigner;
    private final JWSVerifier refreshTokenVerifier;

    public JwtTokenProvider(
            @Value("${jwt.access-token.secret}") String accessTokenSecret,
            @Value("${jwt.access-token.exp}") int accessTokenExpirationMs,
            @Value("${jwt.refresh-token.secret}") String refreshTokenSecret,
            @Value("${jwt.refresh-token.exp}") int refreshTokenExpirationMs
    ) throws JOSEException {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;

        byte[] accessTokenSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenSigner = new MACSigner(accessTokenSecretBytes);
        this.accessTokenVerifier = new MACVerifier(accessTokenSecretBytes);

        byte[] refreshTokenSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenSigner = new MACSigner(refreshTokenSecretBytes);
        this.refreshTokenVerifier = new MACVerifier(refreshTokenSecretBytes);
    }

    private String generateToken(DiscodeitUserDetails userDetails, JWSSigner signer, int expirationMs, String tokenType ) throws JOSEException {
        String tokenId = UUID.randomUUID().toString();

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMs);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .jwtID(tokenId)
                .issueTime(now)
                .expirationTime(expirationDate)
                .claim("userId",userDetails.getUserDto().id())
                .claim("tokenType",tokenType)
                .claim("roles",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private boolean verifyToken(String token, JWSVerifier verifier, String expectedType){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if(!signedJWT.verify(verifier)){
                return false;
            }

            String tokenType = signedJWT.getJWTClaimsSet().getClaim("tokenType").toString();
            if(!tokenType.equals(expectedType)){
                return false;
            }
            Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationDate != null && expirationDate.after(new Date());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(DiscodeitUserDetails userDetails, HttpServletResponse response) throws JOSEException {
        String refreshToken = generateToken(userDetails, refreshTokenSigner, refreshTokenExpirationMs, "refresh");
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpirationMs/1000);
        response.addCookie(cookie);
        return refreshToken;
    }

    public String generateAccessToken(DiscodeitUserDetails userDetails, HttpServletResponse response) throws JOSEException {
        return generateToken(userDetails, accessTokenSigner, accessTokenExpirationMs, "access");
    }

    public boolean verifyAccessToken(String token){
        return verifyToken(token, accessTokenVerifier, "access");
    }

    public boolean verifyRefreshToken(String token){
        return verifyToken(token, refreshTokenVerifier, "refresh");
    }

    public void expireRefreshTokenCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            return signedJwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token",e);
        }
    }

    public String extractTokenId(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            return signedJwt.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT Token",e);
        }
    }

    public String extractUserId(String token) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            return signedJwt.getJWTClaimsSet().getClaim("userId").toString();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT Token",e);
        }
    }



}
