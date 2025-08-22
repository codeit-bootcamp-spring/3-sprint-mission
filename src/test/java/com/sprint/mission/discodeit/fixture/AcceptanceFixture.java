package com.sprint.mission.discodeit.fixture;

import static com.sprint.mission.discodeit.support.TestUtils.json;
import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;
import static com.sprint.mission.discodeit.support.TestUtils.multipartHeader;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.support.AuthTestUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AcceptanceFixture {

  public static ResponseEntity<UserResponse> createUser(
      TestRestTemplate restTemplate,
      String username,
      String email,
      String password,
      String profileImagePath
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userCreateRequest", new HttpEntity<>(json("""
        {
          "username": "%s",
          "email": "%s",
          "password": "%s"
        }
        """.formatted(username, email, password)), jsonHeader()));
    body.add("profile", new ClassPathResource(profileImagePath));

    HttpHeaders headers = multipartHeader();

    return restTemplate.postForEntity(
        "/api/users", new HttpEntity<>(body, headers), UserResponse.class
    );
  }

  public static ResponseEntity<UserResponse> updateUser(
      TestRestTemplate restTemplate,
      UUID userId,
      String newUsername,
      String newEmail,
      String newPassword,
      String newProfileImagePath,
      HttpHeaders sessionHeaders
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userUpdateRequest", new HttpEntity<>(json("""
        {
          "newUsername": "%s",
          "newEmail": "%s",
          "newPassword": "%s"
        }
        """.formatted(newUsername, newEmail, newPassword)), jsonHeader()));
    body.add("profile", new ClassPathResource(newProfileImagePath));
    HttpHeaders headers = multipartHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.exchange(
        "/api/users/" + userId,
        HttpMethod.PATCH,
        new HttpEntity<>(body, headers),
        UserResponse.class
    );
  }

  public static ResponseEntity<MessageResponse> createMessage(
      TestRestTemplate restTemplate,
      UUID userId,
      UUID channelId
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("messageCreateRequest", new HttpEntity<>(json("""
        {
          "content": "첨부 메시지 테스트",
          "authorId": "%s",
          "channelId": "%s"
        }
        """.formatted(userId, channelId)), jsonHeader()));
    body.add("attachments", new ClassPathResource("images/img_01.png"));
    body.add("attachments", new ClassPathResource("images/img_02.png"));
    HttpHeaders headers = multipartHeader();
    return restTemplate.postForEntity(
        "/api/messages", new HttpEntity<>(body, headers), MessageResponse.class);
  }

  public static ResponseEntity<MessageResponse> createMessageAuthenticated(
      TestRestTemplate restTemplate,
      UUID userId,
      UUID channelId,
      HttpHeaders sessionHeaders
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("messageCreateRequest", new HttpEntity<>(json("""
        {
          "content": "첨부 메시지 테스트",
          "authorId": "%s",
          "channelId": "%s"
        }
        """.formatted(userId, channelId)), jsonHeader()));
    body.add("attachments", new ClassPathResource("images/img_01.png"));
    body.add("attachments", new ClassPathResource("images/img_02.png"));
    HttpHeaders headers = multipartHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.postForEntity(
        "/api/messages", new HttpEntity<>(body, headers), MessageResponse.class);
  }

  public static HttpHeaders login(TestRestTemplate restTemplate, String username, String password) {
    String accessToken = AuthTestUtils.loginAndGetAccessToken(restTemplate, username, password);
    return AuthTestUtils.bearerAuthHeaders(accessToken);
  }

  public static ResponseEntity<ChannelResponse> createPublicChannel(
      TestRestTemplate restTemplate,
      HttpHeaders sessionHeaders,
      String name,
      String description
  ) {
    var request = Map.of("name", name, "description", description);
    HttpHeaders headers = jsonHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.postForEntity(
        "/api/channels/public",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);
  }

  public static ResponseEntity<ChannelResponse> createPrivateChannel(
      TestRestTemplate restTemplate,
      HttpHeaders sessionHeaders,
      List<UUID> participantIds
  ) {
    var request = Map.of("participantIds", participantIds);
    HttpHeaders headers = jsonHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.postForEntity(
        "/api/channels/private",
        new HttpEntity<>(request, headers),
        ChannelResponse.class);
  }

  public static ResponseEntity<ChannelResponse> updateChannel(
      TestRestTemplate restTemplate,
      UUID channelId,
      String newName,
      String newDescription,
      HttpHeaders sessionHeaders
  ) {
    var request = Map.of("newName", newName, "newDescription", newDescription);
    HttpHeaders headers = jsonHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.exchange(
        "/api/channels/" + channelId,
        HttpMethod.PATCH,
        new HttpEntity<>(request, headers),
        ChannelResponse.class);
  }

  public static ResponseEntity<MessageResponse> updateMessage(
      TestRestTemplate restTemplate,
      UUID messageId,
      String newContent,
      HttpHeaders sessionHeaders
  ) {
    var request = Map.of("newContent", newContent);
    HttpHeaders headers = jsonHeader();
    headers.addAll(sessionHeaders);
    return restTemplate.exchange(
        "/api/messages/" + messageId,
        HttpMethod.PATCH,
        new HttpEntity<>(request, headers),
        MessageResponse.class);
  }
}
