package com.sprint.mission.discodeit.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 인증/권한 부여 관련 AcceptanceTest 공통 유틸리티.
 */
public final class AuthTestUtils {

  private AuthTestUtils() {
  }

  /**
   * JWT 로그인 후 accessToken 추출
   */
  public static String loginAndGetAccessToken(TestRestTemplate restTemplate, String username,
      String password) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    String form = "username=" + URLEncoder.encode(username, UTF_8) +
        "&password=" + URLEncoder.encode(password, UTF_8);
    ResponseEntity<Map> response = restTemplate.postForEntity(
        "/api/auth/login", new HttpEntity<>(form, headers), Map.class);
    return (String) response.getBody().get("accessToken");
  }

  /**
   * Authorization 헤더 생성
   */
  public static HttpHeaders bearerAuthHeaders(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    return headers;
  }

  /**
   * 관리자 권한 변경(JWT 기반)
   */
  public static void grantRole(TestRestTemplate restTemplate, HttpHeaders adminAuthHeaders,
      UUID targetUserId, String newRole) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.addAll(adminAuthHeaders);

    Map<String, Object> body = Map.of(
        "userId", targetUserId.toString(),
        "newRole", newRole
    );

    ResponseEntity<?> resp = restTemplate.exchange(
        "/api/auth/role",
        HttpMethod.PUT,
        new HttpEntity<>(body, headers),
        Object.class
    );
    Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
