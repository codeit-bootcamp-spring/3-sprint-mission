package com.sprint.mission.discodeit.acceptance.message;

import static com.sprint.mission.discodeit.support.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;

/**
 * Message API에 대한 인수 테스트
 * <p>
 * `test` 프로필로 실행됩니다.
 *
 * <ol>
 * <li>사용자_1_생성</li>
 * <li>사용자_2_생성</li>
 * <li>공개_채널_생성</li>
 * <li>비공개_채널_생성</li>
 * <li>메시지_생성</li>
 * <li>메시지_수정</li>
 * <li>특정_채널_메시지_조회</li>
 * <li>메시지_삭제</li>
 * </ol>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnableJpaAuditing
@Transactional
public class MessageAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId1;
  static UUID userId2;
  static UUID publicChannelId;
  static UUID privateChannelId;
  static UUID messageId;

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

    Assertions.assertNotNull(response.getBody());
    userId1 = response.getBody().id();
  }

  @Test
  @Order(2)
  void 사용자_2_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰2",
        "test2@test.com",
        "images/img_02.png");

    Assertions.assertNotNull(response.getBody());
    userId2 = response.getBody().id();
  }

  @Test
  @Order(3)
  void 공개_채널_생성() {
    var request = Map.of("name", "general", "description", "공개 채널입니다");
    var headers = jsonHeader();

    var response = restTemplate.postForEntity(
        "/api/channels/public",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    publicChannelId = response.getBody().id();
  }

  @Test
  @Order(4)
  void 비공개_채널_생성() {
    var request = Map.of("participantIds", List.of(userId1, userId2));
    var headers = jsonHeader();

    var response = restTemplate.postForEntity(
        "/api/channels/private",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertNotNull(response.getBody());
    privateChannelId = response.getBody().id();
  }

  @Test
  @Order(5)
  void 메시지_생성() {
    ResponseEntity<MessageResponse> response = AcceptanceFixture.createMessage(
        restTemplate,
        userId1,
        publicChannelId);

    Assertions.assertNotNull(response.getBody());
    messageId = response.getBody().id();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @Order(6)
  void 메시지_수정() {
    var updateRequest = Map.of("newContent", "수정된 메시지입니다.");

    HttpHeaders headers = jsonHeader();

    ResponseEntity<MessageResponse> response = restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.PATCH,
        new HttpEntity<>(updateRequest, headers),
        MessageResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isEqualTo("수정된 메시지입니다.");
  }

  @Test
  @Order(7)
  void 특정_채널_메시지_조회() {
    ResponseEntity<PageResponse<MessageResponse>> response = restTemplate.exchange(
        "/api/messages?channelId=" + publicChannelId,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isNotEmpty();

    boolean containsMessage = response.getBody().content().stream()
        .map(MessageResponse::id)
        .anyMatch(id -> id.equals(messageId));

    assertThat(containsMessage).isTrue();
  }

  @Test
  @Order(8)
  void 메시지_삭제() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.DELETE,
        null,
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
