package com.sprint.mission.discodeit.acceptance.senario;


import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.fixture.AcceptanceFixture;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Discodeit에 대한 플로우 인수 테스트
 * <p>
 * `test` 프로필로 실행됩니다.
 *
 * <ol>
 *  <li>사용자_1_생성</li>
 *  <li>사용자_2_생성</li>
 *  <li>사용자_1_수정</li>
 *  <li>사용자_2_수정</li>
 *  <li>전체_유저_조회</li>
 *  <li>사용자_1_로그인</li>
 *  <li>사용자_2_로그인</li>
 *  <li>공개_채널_생성</li>
 *  <li>비공개_채널_생성</li>
 *  <li>공개_채널_수정</li>
 *  <li>사용자_아이디로_채널_조회</li>
 *  <li>이미지를_첨부한_메시지_생성</li>
 *  <li>메시지_수정</li>
 *  <li>메시지_조회</li>
 *  <li>메시지_수신_정보_조회</li>
 *  <li>파일_단건_조회</li>
 *  <li>파일_다건_조회</li>
 *  <li>메시지_삭제</li>
 *  <li>채널_삭제</li>
 *  <li>유저_삭제</li>
 * </ol>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DiscodeitAcceptanceTest {

  @Autowired
  TestRestTemplate restTemplate;

  static UUID userId1;
  static UUID userId2;
  static UUID publicChannelId;
  static UUID privateChannelId;
  static UUID messageId;
  static Set<UUID> attachmentIds;

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
        "images/img_01.png"
    );

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
        "images/img_02.png"
    );

    Assertions.assertNotNull(response.getBody());
    userId2 = response.getBody().id();
  }

  @Test
  @Order(3)
  void 사용자_1_수정() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId1,
        "updatedName",
        "updated@test.com",
        "images/img_02.png"
    );

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isEqualTo("updatedName");
  }

  @Test
  @Order(4)
  void 사용자_2_수정() {
    ResponseEntity<UserResponse> response = AcceptanceFixture.updateUser(
        restTemplate,
        userId2,
        "updatedName2",
        "updated2@test.com",
        "images/img_01.png"
    );

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isEqualTo("updatedName2");
  }

  @Test
  @Order(5)
  void 전체_유저_조회() {
    var response = restTemplate.exchange("/api/users", HttpMethod.GET, null,
        new ParameterizedTypeReference<List<UserResponse>>() {
        });

    assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @Order(6)
  void 사용자_로그인() {
    var loginRequest1 = Map.of(
        "username", "updatedName",
        "password", "pwd123"
    );

    var loginRequest2 = Map.of(
        "username", "updatedName2",
        "password", "pwd123"
    );

    ResponseEntity<UserResponse> response = restTemplate.postForEntity(
        "/api/auth/login",
        new HttpEntity<>(loginRequest1, jsonHeader()),
        UserResponse.class
    );

    ResponseEntity<UserResponse> response2 = restTemplate.postForEntity(
        "/api/auth/login",
        new HttpEntity<>(loginRequest2, jsonHeader()),
        UserResponse.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(7)
  void 공개_채널_생성() {
    var request = Map.of("name", "general", "description", "공개 채널입니다");
    var response = restTemplate.postForEntity("/api/channels/public",
        new HttpEntity<>(request, jsonHeader()), ChannelResponse.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    publicChannelId = response.getBody().id();
  }


  @Test
  @Order(8)
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
  @Order(9)
  void 비공개_채널_생성() {
    var request = Map.of("participantIds", List.of(userId1, userId2));
    var response = restTemplate.postForEntity("/api/channels/private",
        new HttpEntity<>(request, jsonHeader()), ChannelResponse.class);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    privateChannelId = response.getBody().id();
  }


  @Test
  @Order(10)
  void 이미지를_첨부한_메시지_생성() {
    ResponseEntity<Message> response = AcceptanceFixture.createMessage(
        restTemplate,
        userId1,
        publicChannelId
    );

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    messageId = response.getBody().getId();
    attachmentIds = response.getBody().getAttachmentIds();
  }

  @Test
  @Order(11)
  void 메시지_수정() {
    var updateRequest = Map.of("newContent", "수정된 메시지입니다.");
    var response = restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.PATCH,
        new HttpEntity<>(updateRequest, jsonHeader()),
        Message.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEqualTo("수정된 메시지입니다.");
  }

  @Test
  @Order(12)
  void 메시지_조회() {
    var response = restTemplate.exchange(
        "/api/messages?channelId=" + publicChannelId,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Message>>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).extracting(Message::getId).contains(messageId);
  }

  @Test
  @Order(13)
  void 메시지_수신_정보_조회() {
    var response = restTemplate.exchange(
        "/api/readStatuses?userId=" + userId1,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Map<String, Object>>>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @Order(14)
  void 파일_단건_조회() {
    var response = restTemplate.exchange(
        "/api/binaryContents/" + attachmentIds.iterator().next().toString(),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<BinaryContent>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @Order(15)
  void 파일_다건_조회() {
    String query = attachmentIds.stream()
        .map(id -> "binaryContentIds=" + id)
        .reduce((a, b) -> a + "&" + b)
        .orElse("");

    String url = "/api/binaryContents?" + query;

    ResponseEntity<List<BinaryContent>> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<BinaryContent>>() {
        }
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(attachmentIds.size());
  }

  @Test
  @Order(16)
  void 메시지_삭제() {
    var response = restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.DELETE,
        null,
        Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @Order(17)
  void 채널_삭제() {
    var response1 = restTemplate.exchange(
        "/api/channels/" + publicChannelId,
        HttpMethod.DELETE,
        null,
        Void.class);

    var response2 = restTemplate.exchange(
        "/api/channels/" + privateChannelId,
        HttpMethod.DELETE,
        null,
        Void.class);

    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @Order(18)
  void 유저_삭제() {
    ResponseEntity<Void> response1 = restTemplate.exchange(
        "/api/users/" + userId1, HttpMethod.DELETE, null, Void.class
    );
    ResponseEntity<Void> response2 = restTemplate.exchange(
        "/api/users/" + userId2, HttpMethod.DELETE, null, Void.class
    );
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
