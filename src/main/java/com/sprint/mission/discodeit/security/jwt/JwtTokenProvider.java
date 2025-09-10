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
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final UserDetailsService userDetailsService;

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final int accessTokenExpirationMs;
  private final int refreshTokenExpirationMs;

  private final JWSSigner accessTokenSigner;
  private final JWSVerifier accessTokenVerifier;
  private final JWSSigner refreshTokenSigner;
  private final JWSVerifier refreshTokenVerifier;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
      @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
      @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
      @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs,
      UserDetailsService userDetailsService)
      throws JOSEException {
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    byte[] accessSecretBytes = java.util.Base64.getDecoder().decode(accessTokenSecret);
    this.accessTokenSigner = new MACSigner(accessSecretBytes);
    this.accessTokenVerifier = new MACVerifier(accessSecretBytes);
    byte[] refreshSecretBytes = java.util.Base64.getDecoder().decode(refreshTokenSecret);
    this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
    this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
    this.userDetailsService = userDetailsService;
  }

  public Authentication getAuthentication(String token) {
    if (!validateAccessToken(token)) {
      return null;
    }
    String username = getUsernameFromToken(token);
    if (username == null) {
      return null;
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
  }

  public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
  }

  public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
  }

  private String generateToken(DiscodeitUserDetails userDetails, int expirationMs, JWSSigner signer,
      String tokenType) throws JOSEException {
    String tokenId = UUID.randomUUID().toString();
    UserResponse user = userDetails.getUser();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(user.username())
        .jwtID(tokenId)
        .claim("userId", user.id().toString())
        .claim("type", tokenType)
        .claim("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()))
        .issueTime(now)
        .expirationTime(expiryDate)
        .build();

    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader(JWSAlgorithm.HS256),
        claimsSet
    );

    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  public boolean validateAccessToken(String token) {
    return validateToken(token, accessTokenVerifier, "access");
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token, refreshTokenVerifier, "refresh");
  }

  private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        return false;
      }

      String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
      if (!expectedType.equals(tokenType)) {
        return false;
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        return false;
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Cookie generateRefreshTokenCookie(String refreshToken) {
    Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(refreshTokenExpirationMs / 1000);
    return refreshCookie;
  }

  public Cookie genereateRefreshTokenExpirationCookie() {
    Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true); // 운영 HTTPS 사용
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(0);
    return refreshCookie;
  }

  public String getUsernameFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      return null;
    }
  }
}
