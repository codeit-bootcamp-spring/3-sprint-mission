package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("채널 API 통합 테스트")
/**
 * @DirtiesContext를 사용하는 이유:
 *                  통합 테스트에서는 실제 HTTP 요청이 별도의 트랜잭션에서 실행되어
 *                  테스트 트랜잭션과 격리되므로 @Transactional + @Rollback으로는
 *                  데이터베이스 초기화가 되지 않습니다.
 * 
 *                  AFTER_EACH_TEST_METHOD 옵션을 사용하여 각 테스트 메서드 실행 후
 *                  스프링 애플리케이션 컨텍스트를 재생성함으로써 데이터베이스를
 *                  초기 상태로 되돌려 테스트 간 데이터 격리를 보장합니다.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChannelIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("PUBLIC 채널 생성 - 성공")
  void createPublicChannel_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = TestDataBuilder.createPublicChannelCreateRequest();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelCreateRequest> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<ChannelDto> response = restTemplate.exchange(
        getApiUrl("/channels/public"),
        HttpMethod.POST,
        requestEntity,
        ChannelDto.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().name()).isEqualTo(request.name());
    assertThat(response.getBody().description()).isEqualTo(request.description());
    assertThat(response.getBody().type()).isEqualTo(ChannelType.PUBLIC);
    assertThat(response.getBody().id()).isNotNull();
  }

  @Test
  @DisplayName("PUBLIC 채널 생성 - 실패 (유효하지 않은 요청)")
  void createPublicChannel_Fail_InvalidRequest() throws Exception {
    // given
    PublicChannelCreateRequest invalidRequest = new PublicChannelCreateRequest("", "");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelCreateRequest> requestEntity = new HttpEntity<>(invalidRequest, headers);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/channels/public"),
        HttpMethod.POST,
        requestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 - 성공")
  void createPrivateChannel_Success() throws Exception {
    // given - 먼저 사용자 2명 생성
    UUID user1Id = createTestUser("user1", "user1@example.com");
    UUID user2Id = createTestUser("user2", "user2@example.com");

    PrivateChannelCreateRequest request = TestDataBuilder.createPrivateChannelCreateRequest(
        List.of(user1Id, user2Id));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PrivateChannelCreateRequest> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<ChannelDto> response = restTemplate.exchange(
        getApiUrl("/channels/private"),
        HttpMethod.POST,
        requestEntity,
        ChannelDto.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().type()).isEqualTo(ChannelType.PRIVATE);
    assertThat(response.getBody().id()).isNotNull();
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 - 실패 (중복된 참가자)")
  void createPrivateChannel_Fail_DuplicateParticipants() throws Exception {
    // given
    UUID userId = createTestUser("user1", "user1@example.com");

    PrivateChannelCreateRequest request = TestDataBuilder.createPrivateChannelCreateRequest(
        List.of(userId, userId) // 중복된 참가자
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PrivateChannelCreateRequest> requestEntity = new HttpEntity<>(request, headers);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/channels/private"),
        HttpMethod.POST,
        requestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("PUBLIC 채널 수정 - 성공")
  void updatePublicChannel_Success() throws Exception {
    // given - 먼저 PUBLIC 채널 생성
    PublicChannelCreateRequest createRequest = TestDataBuilder.createPublicChannelCreateRequest();

    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelCreateRequest> createRequestEntity = new HttpEntity<>(createRequest, createHeaders);

    ResponseEntity<ChannelDto> createResponse = restTemplate.exchange(
        getApiUrl("/channels/public"),
        HttpMethod.POST,
        createRequestEntity,
        ChannelDto.class);

    UUID channelId = createResponse.getBody().id();

    // given - 수정 요청 준비
    PublicChannelUpdateRequest updateRequest = TestDataBuilder.createPublicChannelUpdateRequest();

    HttpHeaders updateHeaders = new HttpHeaders();
    updateHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest, updateHeaders);

    // when
    ResponseEntity<ChannelResponse> response = restTemplate.exchange(
        getApiUrl("/channels/" + channelId),
        HttpMethod.PATCH,
        updateRequestEntity,
        ChannelResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().name()).isEqualTo(updateRequest.newName());
    assertThat(response.getBody().description()).isEqualTo(updateRequest.newDescription());
    assertThat(response.getBody().id()).isEqualTo(channelId);
  }

  @Test
  @DisplayName("PRIVATE 채널 수정 - 실패 (PRIVATE 채널은 수정 불가)")
  void updatePrivateChannel_Fail_PrivateChannelUpdateNotAllowed() throws Exception {
    // given - 먼저 PRIVATE 채널 생성
    UUID user1Id = createTestUser("user1", "user1@example.com");
    UUID user2Id = createTestUser("user2", "user2@example.com");

    PrivateChannelCreateRequest createRequest = TestDataBuilder.createPrivateChannelCreateRequest(
        List.of(user1Id, user2Id));

    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PrivateChannelCreateRequest> createRequestEntity = new HttpEntity<>(createRequest, createHeaders);

    ResponseEntity<ChannelDto> createResponse = restTemplate.exchange(
        getApiUrl("/channels/private"),
        HttpMethod.POST,
        createRequestEntity,
        ChannelDto.class);

    UUID channelId = createResponse.getBody().id();

    // given - 수정 요청 준비
    PublicChannelUpdateRequest updateRequest = TestDataBuilder.createPublicChannelUpdateRequest();

    HttpHeaders updateHeaders = new HttpHeaders();
    updateHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelUpdateRequest> updateRequestEntity = new HttpEntity<>(updateRequest, updateHeaders);

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/channels/" + channelId),
        HttpMethod.PATCH,
        updateRequestEntity,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("채널 삭제 - 성공")
  void deleteChannel_Success() throws Exception {
    // given - 먼저 채널 생성
    PublicChannelCreateRequest createRequest = TestDataBuilder.createPublicChannelCreateRequest();

    HttpHeaders createHeaders = new HttpHeaders();
    createHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<PublicChannelCreateRequest> createRequestEntity = new HttpEntity<>(createRequest, createHeaders);

    ResponseEntity<ChannelDto> createResponse = restTemplate.exchange(
        getApiUrl("/channels/public"),
        HttpMethod.POST,
        createRequestEntity,
        ChannelDto.class);

    UUID channelId = createResponse.getBody().id();

    // when
    ResponseEntity<Void> response = restTemplate.exchange(
        getApiUrl("/channels/" + channelId),
        HttpMethod.DELETE,
        null,
        Void.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("채널 삭제 - 실패 (존재하지 않는 채널)")
  void deleteChannel_Fail_ChannelNotFound() throws Exception {
    // given
    UUID nonExistentChannelId = UUID.randomUUID();

    // when
    ResponseEntity<ErrorResponse> response = restTemplate.exchange(
        getApiUrl("/channels/" + nonExistentChannelId),
        HttpMethod.DELETE,
        null,
        ErrorResponse.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
  }

  /**
   * 테스트용 사용자 생성 헬퍼 메서드
   */
  private UUID createTestUser(String username, String email) throws Exception {
    UserCreateRequest request = TestDataBuilder.createUserCreateRequest(username, email, "password123");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // JSON 데이터를 HttpEntity로 래핑하여 Content-Type 지정
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
}