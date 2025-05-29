package com.sprint.mission.discodeit.acceptance.channel;

import static com.sprint.mission.discodeit.support.TestUtils.json;
import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Channel API에 대한 인수 테스트
 * <p>
 * `test` 프로필로 실행됩니다.
 *
 * <ol>
 *   <li>사용자_생성</li>
 *   <li>공개_채널_생성</li>
 *   <li>비공개_채널_생성</li>
 *   <li>특정_유저의_채널_조회</li>
 *   <li>공개_채널_수정</li>
 *   <li>채널_삭제</li>
 *   <li>사용자_삭제</li>
 * </ol>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(JpaAuditingConfig.class)
class ChannelAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;
  static UUID publicChannelId;
  static UUID privateChannelId;

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
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userCreateRequest", new HttpEntity<>(json("""
            {
              "username": "길동쓰",
              "email": "test@test.com",
              "password": "pw123"
            }
        """), jsonHeader()));
    body.add("profile", new ClassPathResource("images/img_02.png"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    ResponseEntity<UserResponse> response = restTemplate.postForEntity(
        "/api/users", new HttpEntity<>(body, headers), UserResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    userId = response.getBody().id();
  }

  @Test
  @Order(2)
  void 공개_채널_생성() {
    var request = Map.of("name", "general", "description", "공개 채널입니다");
    var headers = jsonHeader();

    var response = restTemplate.postForEntity(
        "/api/channels/public",
        new HttpEntity<>(request, headers),
        ChannelResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    publicChannelId = response.getBody().id();
  }

  @Test
  @Order(3)
  void 비공개_채널_생성() {
    var request = Map.of("participantIds", List.of(userId));
    var headers = jsonHeader();

    var response = restTemplate.postForEntity(
        "/api/channels/private",
        new HttpEntity<>(request, headers),
        ChannelResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    privateChannelId = response.getBody().id();
  }

  @Test
  @Order(4)
  void 특정_유저의_채널_조회() {
    var response = restTemplate.exchange(
        "/api/channels?userId=" + userId,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<ChannelResponse>>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  @Order(5)
  void 공개_채널_수정() {
    var request = Map.of("newName", "updated-channel", "newDescription", "수정된 설명");
    var headers = jsonHeader();

    var response = restTemplate.exchange(
        "/api/channels/" + publicChannelId,
        HttpMethod.PATCH,
        new HttpEntity<>(request, headers),
        ChannelResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertNotNull(response.getBody());
    assertThat(response.getBody().name()).isEqualTo("updated-channel");
  }

  @Test
  @Order(6)
  void 채널_삭제() {
    var res1 = restTemplate.exchange(
        "/api/channels/" + publicChannelId, HttpMethod.DELETE, null, Void.class);
    var res2 = restTemplate.exchange(
        "/api/channels/" + privateChannelId, HttpMethod.DELETE, null, Void.class);

    assertThat(res1.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @Order(7)
  void 사용자_삭제() {
    var deleteUser = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, null, Void.class);

    assertThat(deleteUser.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
