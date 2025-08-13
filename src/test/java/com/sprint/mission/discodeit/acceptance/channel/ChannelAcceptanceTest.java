package com.sprint.mission.discodeit.acceptance.channel;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
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

@ActiveProfiles("security-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "discodeit.security.disable-csrf=true"
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ChannelAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;
  static UUID publicChannelId;
  static UUID privateChannelId;

  private HttpHeaders userSessionHeaders;
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
  void 사용자_생성() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.createUser(
        restTemplate,
        "길동쓰",
        "test@test.com",
        "images/img_02.png");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var created = Objects.requireNonNull(response.getBody());
    userId = created.id();
    username = created.username();
    // 동시 세션 방지 정책 때문에 역할 부여 후 최초 로그인 수행
  }

  @Test
  @Order(2)
  void 공개_채널_생성() {
    // CHANNEL_MANAGER 권한 부여 (사용자 세션 아직 없음)
    AuthTestUtils.grantRole(restTemplate, adminSessionHeaders, userId, "CHANNEL_MANAGER");
    userSessionHeaders = AcceptanceFixture.login(restTemplate, username, TEST_PASSWORD);
    assertThat(userSessionHeaders.getFirst(HttpHeaders.COOKIE)).isNotBlank();

    var response = AcceptanceFixture.createPublicChannel(
        restTemplate,
        userSessionHeaders,
        "general",
        "공개 채널입니다");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var createdPublicChannel = Objects.requireNonNull(response.getBody());
    publicChannelId = createdPublicChannel.id();
  }

  @Test
  @Order(3)
  void 비공개_채널_생성() {
    System.out.println(
        "[DEBUG] 비공개_채널_생성 직전 세션 쿠키=" + userSessionHeaders.getFirst(HttpHeaders.COOKIE));
    var response = AcceptanceFixture.createPrivateChannel(
        restTemplate,
        userSessionHeaders,
        List.of(userId));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var createdPrivateChannel = Objects.requireNonNull(response.getBody());
    privateChannelId = createdPrivateChannel.id();
  }

  @Test
  @Order(4)
  void 특정_유저의_채널_조회() {
    System.out.println(
        "[DEBUG] 특정_유저의_채널_조회 직전 세션 쿠키=" + userSessionHeaders.getFirst(HttpHeaders.COOKIE));
    var response = restTemplate.exchange(
        "/api/channels?userId=" + userId,
        HttpMethod.GET,
        new HttpEntity<Void>(userSessionHeaders),
        new ParameterizedTypeReference<List<ChannelResponse>>() {
        });

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  @Order(5)
  void 공개_채널_수정() {
    System.out.println(
        "[DEBUG] 공개_채널_수정 직전 세션 쿠키=" + userSessionHeaders.getFirst(HttpHeaders.COOKIE));
    var response = AcceptanceFixture.updateChannel(
        restTemplate,
        publicChannelId,
        "updated-channel",
        "수정된 설명",
        userSessionHeaders);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    var updatedChannel = Objects.requireNonNull(response.getBody());
    assertThat(updatedChannel.name()).isEqualTo("updated-channel");
  }

  @Test
  @Order(6)
  void 채널_삭제() {
    var res1 = restTemplate.exchange(
        "/api/channels/" + publicChannelId, HttpMethod.DELETE,
        new HttpEntity<Void>(userSessionHeaders),
        Void.class);
    var res2 = restTemplate.exchange(
        "/api/channels/" + privateChannelId, HttpMethod.DELETE,
        new HttpEntity<Void>(userSessionHeaders), Void.class);

    assertThat(res1.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(res2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @Order(7)
  void 사용자_삭제() {
    var deleteUser = restTemplate.exchange(
        "/api/users/" + userId, HttpMethod.DELETE, new HttpEntity<Void>(userSessionHeaders),
        Void.class);

    assertThat(deleteUser.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
