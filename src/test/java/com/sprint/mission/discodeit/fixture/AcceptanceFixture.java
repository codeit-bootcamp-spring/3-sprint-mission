package com.sprint.mission.discodeit.fixture;

import static com.sprint.mission.discodeit.support.TestUtils.json;
import static com.sprint.mission.discodeit.support.TestUtils.jsonHeader;

import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.UUID;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AcceptanceFixture {

  public static ResponseEntity<UserResponse> createUser(
      TestRestTemplate restTemplate,
      String username,
      String email,
      String profileImagePath
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userCreateRequest", new HttpEntity<>(json("""
        {
          "username": "%s",
          "email": "%s",
          "password": "pw123"
        }
        """.formatted(username, email)), jsonHeader()));
    body.add("profile", new ClassPathResource(profileImagePath));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    return restTemplate.postForEntity(
        "/api/users", new HttpEntity<>(body, headers), UserResponse.class
    );
  }

  public static ResponseEntity<UserResponse> updateUser(
      TestRestTemplate restTemplate,
      UUID userId,
      String newUsername,
      String newEmail,
      String newProfileImagePath
  ) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("userUpdateRequest", new HttpEntity<>(json("""
        {
          "newUsername": "%s",
          "newEmail": "%s",
          "newPassword": "pwd123"
        }
        """.formatted(newUsername, newEmail)), jsonHeader()));
    body.add("profile", new ClassPathResource(newProfileImagePath));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

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

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    return restTemplate.postForEntity(
        "/api/messages", new HttpEntity<>(body, headers), MessageResponse.class);
  }
}
