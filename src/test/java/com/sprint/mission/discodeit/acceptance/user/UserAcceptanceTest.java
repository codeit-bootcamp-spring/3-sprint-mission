package com.sprint.mission.discodeit.acceptance.user;

import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import com.sprint.mission.discodeit.support.AuthTestUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ActiveProfiles("security-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "discodeit.security.disable-csrf=true"
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class UserAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;
  static HttpHeaders userSessionHeaders = new HttpHeaders();
  private static final String TEST_PASSWORD = "pw123";

  @TempDir
  static Path tempDir;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("discodeit.repository.file-directory.folder",
        () -> tempDir.toAbsolutePath().toString());
  }

  @Test
  @Order(1)
  void 사용자_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰",
        "test@test.com",
        "images/img_01.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final UserResponse body = Objects.requireNonNull(response.getBody());
    userId = body.id();

    HttpHeaders loggedIn = AuthTestUtils.formLogin(restTemplate, body.username(), TEST_PASSWORD);
    userSessionHeaders.set(HttpHeaders.COOKIE, loggedIn.getFirst(HttpHeaders.COOKIE));
  }

  @Test
  @Order(2)
  void 사용자_수정() {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userUpdateRequest", new HttpEntity<>("""
        {
          "newUsername": "updatedName",
          "newEmail": "updated@test.com",
          "newPassword": "pwd123"
        }
        """.stripIndent(), jsonHeader()));
    body.add("profile", new ClassPathResource("images/img_02.png"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.addAll(userSessionHeaders);

    ResponseEntity<UserResponse> response = restTemplate.exchange(
        "/api/users/" + userId,
        HttpMethod.PATCH,
        new HttpEntity<>(body, headers),
        UserResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final UserResponse updated = Objects.requireNonNull(response.getBody());
    assertThat(updated.username()).isEqualTo("updatedName");
  }

  @Test
  @Order(3)
  void 사용자_전체_조회() {
    ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
        "/api/users", HttpMethod.GET, new HttpEntity<Void>(userSessionHeaders),
        new ParameterizedTypeReference<List<UserResponse>>() {
        });
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  @Order(4)
  void 사용자_삭제() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, new HttpEntity<Void>(userSessionHeaders),
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
