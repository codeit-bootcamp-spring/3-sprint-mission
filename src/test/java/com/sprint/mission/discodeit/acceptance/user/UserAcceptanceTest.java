package com.sprint.mission.discodeit.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import java.nio.file.Path;
import java.util.List;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * User API에 대한 인수 테스트
 * <p>
 * `test` 프로필로 실행됩니다.
 *
 * <ol>
 *   <li>사용자_생성</li>
 *   <li>사용자_수정</li>
 *   <li>사용자_상태_변경</li>
 *   <li>사용자_전체_조회</li>
 *   <li>사용자_삭제</li>
 * </ol>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableJpaAuditing
public class UserAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;

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
        "images/img_01.png"
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    userId = response.getBody().id();
  }

  @Test
  @Order(2)
  void 사용자_수정() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId,
        "updatedName",
        "updated@test.com",
        "images/img_02.png"
    );

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isEqualTo("updatedName");
  }

  @Test
  @Order(3)
  void 사용자_상태_변경() {
    ResponseEntity<UserStatus> response = restTemplate.exchange(
        "/api/users/" + userId + "/userStatus", HttpMethod.PATCH, null, UserStatus.class
    );
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(4)
  void 사용자_전체_조회() {
    ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
        "/api/users", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isNotEmpty();
  }

  @Test
  @Order(5)
  void 사용자_삭제() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, null, Void.class
    );
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
