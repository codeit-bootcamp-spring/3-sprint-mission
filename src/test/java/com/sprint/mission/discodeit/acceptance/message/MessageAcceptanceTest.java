package com.sprint.mission.discodeit.acceptance.message;

import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import com.sprint.mission.discodeit.support.AuthTestUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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

@ActiveProfiles("security-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"discodeit.security.disable-csrf=true"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
public class MessageAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;        // 기존 userId1
  static UUID otherUserId;   // 기존 userId2
  static UUID publicChannelId;
  static UUID privateChannelId;
  static UUID messageId;

  private final HttpHeaders userSessionHeaders = new HttpHeaders();
  private final HttpHeaders adminSessionHeaders = new HttpHeaders();
  private String username;
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
  void 사용자_1_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰",
        "test@test.com",
        "images/img_01.png");
    var createdUser1 = Objects.requireNonNull(response.getBody());
    userId = createdUser1.id();
    username = createdUser1.username();
  }

  @Test
  @Order(2)
  void 사용자_2_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰2",
        "test2@test.com",
        "images/img_02.png");
    var createdUser2 = Objects.requireNonNull(response.getBody());
    otherUserId = createdUser2.id();
  }

  @Test
  @Order(3)
  void 공개_채널_생성() {
    // 권한 부여, 로그인
    AuthTestUtils.grantRole(restTemplate, adminSessionHeaders, userId, "CHANNEL_MANAGER");
    HttpHeaders loggedIn = AuthTestUtils.formLogin(restTemplate, username, TEST_PASSWORD);
    userSessionHeaders.set(HttpHeaders.COOKIE, loggedIn.getFirst(HttpHeaders.COOKIE));

    var request = Map.of("name", "general", "description", "공개 채널입니다");
    var headers = jsonHeader();
    headers.addAll(userSessionHeaders);

    var response = restTemplate.postForEntity(
        "/api/channels/public",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var createdPublicChannel = Objects.requireNonNull(response.getBody());
    publicChannelId = createdPublicChannel.id();
  }

  @Test
  @Order(4)
  void 비공개_채널_생성() {
    var request = Map.of("participantIds", List.of(userId, otherUserId));
    var headers = jsonHeader();
    headers.addAll(userSessionHeaders);

    var response = restTemplate.postForEntity(
        "/api/channels/private",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var createdPrivateChannel = Objects.requireNonNull(response.getBody());
    privateChannelId = createdPrivateChannel.id();
  }

  @Test
  @Order(5)
  void 메시지_생성() {
    ResponseEntity<MessageResponse> response = AcceptanceFixture.createMessageAuthenticated(
        restTemplate,
        userId,
        publicChannelId,
        userSessionHeaders);

    var createdMessage = Objects.requireNonNull(response.getBody());
    messageId = createdMessage.id();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @Order(6)
  void 메시지_수정() {
    var updateRequest = Map.of("newContent", "수정된 메시지입니다.");
    HttpHeaders headers = jsonHeader();
    headers.addAll(userSessionHeaders);

    ResponseEntity<MessageResponse> response = restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.PATCH,
        new HttpEntity<>(updateRequest, headers),
        MessageResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var updated = Objects.requireNonNull(response.getBody());
    assertThat(updated.content()).isEqualTo("수정된 메시지입니다.");
  }

  @Test
  @Order(7)
  void 특정_채널_메시지_조회() {
    ResponseEntity<PageResponse<MessageResponse>> response = restTemplate.exchange(
        "/api/messages?channelId=" + publicChannelId,
        HttpMethod.GET,
        new HttpEntity<Void>(userSessionHeaders),
        new ParameterizedTypeReference<>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var page = Objects.requireNonNull(response.getBody());
    assertThat(page.content()).isNotEmpty();

    var pageResult = Objects.requireNonNull(response.getBody());
    boolean containsMessage = pageResult.content().stream()
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
        new HttpEntity<Void>(userSessionHeaders),
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
