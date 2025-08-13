package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("메시지 API 통합 테스트")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("메시지 생성 - 성공")
  void createMessage_Success() throws Exception {
    // given - 테스트 데이터 준비
    UUID userId = createTestUser("testUser", "test@example.com");
    UUID channelId = createTestChannel("Test Channel", "Test Description");

    MessageCreateRequest request = TestDataBuilder.createMessageCreateRequest("Test Message", channelId, userId);

    // Multipart 요청 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("messageCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // when
    ResponseEntity<MessageDto> response = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        requestEntity,
        MessageDto.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isEqualTo(request.content());
    assertThat(response.getBody().author().id()).isEqualTo(request.authorId());
    assertThat(response.getBody().channelId()).isEqualTo(request.channelId());
    assertThat(response.getBody().id()).isNotNull();
  }

  @Test
  @DisplayName("메시지 생성 - 실패 (유효하지 않은 요청)")
  void createMessage_Fail_InvalidRequest() throws Exception {
    // given
    MessageCreateRequest invalidRequest = new MessageCreateRequest("", null, null);

    // Multipart 요청 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(invalidRequest), jsonHeaders);
    body.add("messageCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        requestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("메시지 생성 - 실패 (존재하지 않는 사용자)")
  void createMessage_Fail_UserNotFound() throws Exception {
    // given
    UUID nonExistentUserId = UUID.randomUUID();
    UUID channelId = createTestChannel("Test Channel", "Test Description");

    MessageCreateRequest request = TestDataBuilder.createMessageCreateRequest(
        "Test Message", channelId, nonExistentUserId);

    // Multipart 요청 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("messageCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        requestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("메시지 수정 - 성공")
  void updateMessage_Success() throws Exception {
    // given - 먼저 메시지 생성
    UUID userId = createTestUser("testUser", "test@example.com");
    UUID channelId = createTestChannel("Test Channel", "Test Description");

    MessageCreateRequest createRequest = TestDataBuilder.createMessageCreateRequest(
        "Original Message", channelId, userId);

    // Multipart 요청으로 메시지 생성
    MultiValueMap<String, Object> createBody = new LinkedMultiValueMap<>();
    HttpHeaders createJsonHeaders = new HttpHeaders();
    createJsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> createJsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(createRequest),
        createJsonHeaders);
    createBody.add("messageCreateRequest", createJsonEntity);

    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> createRequestEntity = new HttpEntity<>(createBody, createHeaders);

    ResponseEntity<MessageDto> createResponse = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        createRequestEntity,
        MessageDto.class);

    UUID messageId = createResponse.getBody().id();

    // given - 수정 요청 준비
    MessageUpdateRequest updateRequest = TestDataBuilder.createMessageUpdateRequest();

    HttpHeaders updateHeaders = new HttpHeaders();
    updateHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<MessageUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest, updateHeaders);

    // when
    ResponseEntity<MessageDto> response = restTemplate.exchange(
        getApiUrl("/messages/" + messageId),
        HttpMethod.PATCH,
        updateRequestEntity,
        MessageDto.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isEqualTo(updateRequest.newContent());
    assertThat(response.getBody().id()).isEqualTo(messageId);
  }

  @Test
  @DisplayName("메시지 수정 - 실패 (존재하지 않는 메시지)")
  void updateMessage_Fail_MessageNotFound() throws Exception {
    // given
    UUID nonExistentMessageId = UUID.randomUUID();
    MessageUpdateRequest updateRequest = TestDataBuilder.createMessageUpdateRequest();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<MessageUpdateRequest> requestEntity = new HttpEntity<>(updateRequest, headers);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/messages/" + nonExistentMessageId),
        HttpMethod.PATCH,
        requestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("메시지 삭제 - 성공")
  void deleteMessage_Success() throws Exception {
    // given - 먼저 메시지 생성
    UUID userId = createTestUser("testUser", "test@example.com");
    UUID channelId = createTestChannel("Test Channel", "Test Description");

    MessageCreateRequest createRequest = TestDataBuilder.createMessageCreateRequest(
        "Test Message", channelId, userId);

    // Multipart 요청으로 메시지 생성
    MultiValueMap<String, Object> createBody = new LinkedMultiValueMap<>();
    HttpHeaders createJsonHeaders = new HttpHeaders();
    createJsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> createJsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(createRequest),
        createJsonHeaders);
    createBody.add("messageCreateRequest", createJsonEntity);

    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> createRequestEntity = new HttpEntity<>(createBody, createHeaders);

    ResponseEntity<MessageDto> createResponse = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        createRequestEntity,
        MessageDto.class);

    UUID messageId = createResponse.getBody().id();

    // when
    ResponseEntity<Void> response = restTemplate.exchange(
        getApiUrl("/messages/" + messageId),
        HttpMethod.DELETE,
        null,
        Void.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("메시지 삭제 - 실패 (존재하지 않는 메시지)")
  void deleteMessage_Fail_MessageNotFound() throws Exception {
    // given
    UUID nonExistentMessageId = UUID.randomUUID();

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/messages/" + nonExistentMessageId),
        HttpMethod.DELETE,
        null,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 성공")
  void findMessagesByChannelId_Success() throws Exception {
    // given - 테스트 데이터 준비
    UUID userId = createTestUser("testUser", "test@example.com");
    UUID channelId = createTestChannel("Test Channel", "Test Description");

    // 메시지 2개 생성
    createTestMessage(userId, channelId, "First Message");
    createTestMessage(userId, channelId, "Second Message");

    // when
    ResponseEntity<PageResponse> response = restTemplate.exchange(
        getApiUrl("/messages?channelId=" + channelId + "&page=0&size=10"),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<PageResponse>() {
        });

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isNotEmpty();
    assertThat(response.getBody().content().size()).isGreaterThanOrEqualTo(2);
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 - 빈 결과")
  void findMessagesByChannelId_EmptyResult() throws Exception {
    // given
    UUID channelId = createTestChannel("Empty Channel", "No messages");

    // when
    ResponseEntity<PageResponse> response = restTemplate.exchange(
        getApiUrl("/messages?channelId=" + channelId + "&page=0&size=10"),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<PageResponse>() {
        });

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 채널의 메시지 조회 - 성공 (빈 결과)")
  void findMessagesByChannelId_Fail_ChannelNotFound() throws Exception {
    // given
    UUID nonExistentChannelId = UUID.randomUUID();

    // when
    ResponseEntity<PageResponse> response = restTemplate.exchange(
        getApiUrl("/messages?channelId=" + nonExistentChannelId + "&page=0&size=10"),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<PageResponse>() {
        });

    // then - 존재하지 않는 채널이라도 빈 결과를 반환하는 것이 정상
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().content()).isEmpty();
  }

  /**
   * 테스트용 사용자 생성 헬퍼 메서드
   */
  private UUID createTestUser(String username, String email) throws Exception {
    UserCreateRequest request = TestDataBuilder.createUserCreateRequest(username, email);

    // Multipart 요청 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("userCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<UserResponse> response = restTemplate.exchange(
        getApiUrl("/users"),
        HttpMethod.POST,
        requestEntity,
        UserResponse.class);

    return response.getBody().id();
  }

  /**
   * 테스트용 채널 생성 헬퍼 메서드
   */
  private UUID createTestChannel(String name, String description) throws Exception {
    PublicChannelCreateRequest request = TestDataBuilder.createPublicChannelCreateRequest(name, description);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelCreateRequest> requestEntity = new HttpEntity<>(request, headers);

    ResponseEntity<ChannelDto> response = restTemplate.exchange(
        getApiUrl("/channels/public"),
        HttpMethod.POST,
        requestEntity,
        ChannelDto.class);

    return response.getBody().id();
  }

  /**
   * 테스트용 메시지 생성 헬퍼 메서드
   */
  private UUID createTestMessage(UUID userId, UUID channelId, String content) throws Exception {
    MessageCreateRequest request = TestDataBuilder.createMessageCreateRequest(content, channelId, userId);

    // Multipart 요청 설정
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("messageCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    ResponseEntity<MessageDto> response = restTemplate.exchange(
        getApiUrl("/messages"),
        HttpMethod.POST,
        requestEntity,
        MessageDto.class);

    return response.getBody().id();
  }
}