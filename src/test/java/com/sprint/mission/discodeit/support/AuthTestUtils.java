package com.sprint.mission.discodeit.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLEncoder;
import java.util.List;
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
   * 폼 로그인 후 세션 쿠키(HttpHeaders.COOKIE) 담긴 headers 반환.
   */
  public static HttpHeaders formLogin(TestRestTemplate restTemplate, String username,
      String password) {
    HttpHeaders h = new HttpHeaders();
    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    String form = "username=" + URLEncoder.encode(username, UTF_8) +
        "&password=" + URLEncoder.encode(password, UTF_8);
    ResponseEntity<String> login = restTemplate.postForEntity(
        "/api/auth/login", new HttpEntity<>(form, h), String.class);
    Assertions.assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    HttpHeaders session = new HttpHeaders();
    List<String> setCookies = login.getHeaders().get(HttpHeaders.SET_COOKIE);
    if (setCookies != null && !setCookies.isEmpty()) {
      String cookieHeader = setCookies.stream().map(c -> c.split(";", 2)[0])
          .reduce((a, b) -> a + "; " + b).orElse(null);
      session.set(HttpHeaders.COOKIE, cookieHeader);
    }
    return session;
  }

  /**
   * admin 세션 확보 (AdminInitializer: admin/admin).
   */
  public static void ensureAdminSession(TestRestTemplate restTemplate,
      HttpHeaders adminSessionHeaders) {
    if (adminSessionHeaders.getFirst(HttpHeaders.COOKIE) != null) {
      return;
    }
    HttpHeaders admin = formLogin(restTemplate, "admin", "admin");
    adminSessionHeaders.set(HttpHeaders.COOKIE, admin.getFirst(HttpHeaders.COOKIE));
  }

  /**
   * ADMIN 세션을 사용하여 대상 사용자 권한 변경.
   */
  public static void grantRole(TestRestTemplate restTemplate, HttpHeaders adminSessionHeaders,
      UUID targetUserId, String newRole) {
    ensureAdminSession(restTemplate, adminSessionHeaders);
    HttpHeaders headers = TestUtils.jsonHeader();
    headers.addAll(adminSessionHeaders);
    var body = Map.of(
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
