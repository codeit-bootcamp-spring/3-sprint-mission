package com.sprint.mission.discodeit.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import com.sprint.mission.discodeit.testconfig.AbstractTestKafkaConfig;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserAcceptanceTest extends AbstractTestKafkaConfig {

  @Autowired
  TestRestTemplate restTemplate;

  UUID userId;
  UUID otherUserId;
  HttpHeaders userAuthHeaders;
  HttpHeaders otherAuthHeaders;
  private final String TEST_PASSWORD = "pwd123";

  @Test
  @Order(1)
  void 사용자_1_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰",
        "test@test.com",
        TEST_PASSWORD,
        "images/img_01.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final UserResponse body = Objects.requireNonNull(response.getBody());
    userId = body.id();

    userAuthHeaders = AcceptanceFixture.login(restTemplate, body.username(), TEST_PASSWORD);
  }

  @Test
  @Order(2)
  void 사용자_2_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰2",
        "test2@test.com",
        TEST_PASSWORD,
        "images/img_02.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final UserResponse body = Objects.requireNonNull(response.getBody());
    otherUserId = body.id();

    otherAuthHeaders = AcceptanceFixture.login(restTemplate, body.username(), TEST_PASSWORD);
  }

  @Test
  @Order(3)
  void 사용자_수정() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId,
        "updatedName",
        "updated@test.com",
        TEST_PASSWORD,
        "images/img_02.png",
        userAuthHeaders);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final UserResponse updated = Objects.requireNonNull(response.getBody());
    assertThat(updated.username()).isEqualTo("updatedName");

    // username 변경 후 토큰 갱신
    userAuthHeaders = AcceptanceFixture.login(restTemplate, updated.username(), TEST_PASSWORD);
  }

  @Test
  @Order(4)
  void 다른_사용자_수정_시_실패() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId,
        "hacker",
        "hack@test.com",
        TEST_PASSWORD,
        "images/img_02.png",
        otherAuthHeaders);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @Order(5)
  void 사용자_전체_조회() {
    ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
        "/api/users", HttpMethod.GET, new HttpEntity<Void>(userAuthHeaders),
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
        new HttpEntity<Void>(otherAuthHeaders),
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @Order(7)
  void 사용자_삭제() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, new HttpEntity<Void>(userAuthHeaders),
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
