package com.sprint.mission.discodeit.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Tag("integration")
@ActiveProfiles("security-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
public class UserAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  UUID userId;
  UUID otherUserId;
  HttpHeaders userSessionHeaders;
  HttpHeaders otherSessionHeaders;
  private final String TEST_PASSWORD = "pw123";

  @TempDir
  static Path tempDir;

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("discodeit.repository.file-directory.folder",
        () -> tempDir.toAbsolutePath().toString());
  }

  @Test
  @Order(1)
  void 사용자_1_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰",
        "test@test.com",
        "images/img_01.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final UserResponse body = Objects.requireNonNull(response.getBody());
    userId = body.id();

    userSessionHeaders = AcceptanceFixture.login(restTemplate, body.username(), TEST_PASSWORD);
  }

  @Test
  @Order(2)
  void 사용자_2_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰2",
        "test2@test.com",
        "images/img_02.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final UserResponse body = Objects.requireNonNull(response.getBody());
    otherUserId = body.id();

    otherSessionHeaders = AcceptanceFixture.login(restTemplate, body.username(), TEST_PASSWORD);
  }

  @Test
  @Order(3)
  void 사용자_수정() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId,
        "updatedName",
        "updated@test.com",
        "images/img_02.png",
        userSessionHeaders);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final UserResponse updated = Objects.requireNonNull(response.getBody());
    assertThat(updated.username()).isEqualTo("updatedName");
  }

  @Test
  @Order(4)
  void 다른_사용자_수정_시_실패() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId,
        "hacker",
        "hack@test.com",
        "images/img_02.png",
        otherSessionHeaders);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @Order(5)
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
  @Order(6)
  void 다른_사용자_삭제_시_실패() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userId,
        HttpMethod.DELETE,
        new HttpEntity<Void>(otherSessionHeaders),
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @Order(7)
  void 사용자_삭제() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, new HttpEntity<Void>(userSessionHeaders),
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
