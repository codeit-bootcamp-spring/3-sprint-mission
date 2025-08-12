package com.sprint.mission.discodeit.acceptance.channel;

import static com.sprint.mission.discodeit.support.TestUtils.json;
import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
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

@ActiveProfiles("security-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "discodeit.security.disable-csrf=true"
    })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// 테스트 인스턴스를 재사용하여 JSESSIONID 쿠키 값(HttpHeaders) 유지
@TestInstance(Lifecycle.PER_CLASS)
class ChannelAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId;
  static UUID publicChannelId;
  static UUID privateChannelId;

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
        "/api/users", new HttpEntity<>(body, headers), UserResponse.class);

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
    HttpHeaders loggedIn = AuthTestUtils.formLogin(restTemplate, username,
        TEST_PASSWORD);
    userSessionHeaders.set(HttpHeaders.COOKIE, loggedIn.getFirst(HttpHeaders.COOKIE));
    assertThat(userSessionHeaders.getFirst(HttpHeaders.COOKIE)).isNotBlank();

    var headers = jsonHeader();
    headers.addAll(userSessionHeaders);

    var request = Map.of("name", "general", "description", "공개 채널입니다");
    var response = restTemplate.postForEntity(
        "/api/channels/public",
        new HttpEntity<>(request, headers),
        com.sprint.mission.discodeit.dto.response.ChannelResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    var createdPublicChannel = Objects.requireNonNull(response.getBody());
    publicChannelId = createdPublicChannel.id();
  }

  @Test
  @Order(3)
  void 비공개_채널_생성() {
    System.out.println(
        "[DEBUG] 비공개_채널_생성 직전 세션 쿠키=" + userSessionHeaders.getFirst(HttpHeaders.COOKIE));
    var request = Map.of("participantIds", List.of(userId));
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
    var request = Map.of("newName", "updated-channel", "newDescription", "수정된 설명");
    var headers = jsonHeader();
    headers.addAll(userSessionHeaders);

    var response = restTemplate.exchange(
        "/api/channels/" + publicChannelId,
        HttpMethod.PATCH,
        new HttpEntity<>(request, headers),
        com.sprint.mission.discodeit.dto.response.ChannelResponse.class);

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
