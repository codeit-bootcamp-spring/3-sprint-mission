package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("사용자 API 통합 테스트")
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
class UserIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("사용자 생성 - 성공")
  void createUser_Success() throws Exception {
    // Given
    UserCreateRequest request = TestDataBuilder.createUserCreateRequest();

    // multipart/form-data 요청 구성
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // JSON 데이터를 ByteArrayResource로 래핑하여 Content-Type 지정
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("userCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // When
    ResponseEntity<UserResponse> response = restTemplate.postForEntity(
        "/api/users",
        requestEntity,
        UserResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isEqualTo(request.username());
    assertThat(response.getBody().email()).isEqualTo(request.email());
  }

  @Test
  @DisplayName("사용자 생성 - 실패 (유효하지 않은 요청)")
  void createUser_Fail_InvalidRequest() throws Exception {
    // Given - 잘못된 요청 (빈 사용자명)
    UserCreateRequest request = TestDataBuilder.createUserCreateRequest("",
        "invalid@example.com", "password123");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // JSON 데이터를 HttpEntity로 래핑하여 Content-Type 지정
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("userCreateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // When
    ResponseEntity<String> response = restTemplate.postForEntity(
        "/api/users",
        requestEntity,
        String.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DisplayName("사용자 목록 조회 - 빈 결과")
  void findAllUsers_EmptyResult() {
    // When
    ResponseEntity<List<UserDto>> response = restTemplate.exchange(
        "/api/users",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDto>>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  @DisplayName("사용자 수정 - 성공")
  void updateUser_Success() throws Exception {
    // Given
    UserDto createdUser = createTestUser();
    UserUpdateRequest request = TestDataBuilder.createUserUpdateRequest();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // JSON 데이터를 HttpEntity로 래핑하여 Content-Type 지정
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("userUpdateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // When
    ResponseEntity<UserResponse> response = restTemplate.exchange(
        "/api/users/" + createdUser.id(),
        HttpMethod.PATCH,
        requestEntity,
        UserResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isEqualTo(request.newUsername());
    assertThat(response.getBody().email()).isEqualTo(request.newEmail());
  }

  @Test
  @DisplayName("사용자 수정 - 실패 (존재하지 않는 사용자)")
  void updateUser_Fail_UserNotFound() throws Exception {
    // Given
    UUID nonExistentUserId = UUID.randomUUID();
    UserUpdateRequest request = TestDataBuilder.createUserUpdateRequest();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // JSON 데이터를 HttpEntity로 래핑하여 Content-Type 지정
    HttpHeaders jsonHeaders = new HttpHeaders();
    jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> jsonEntity = new HttpEntity<>(objectMapper.writeValueAsString(request), jsonHeaders);
    body.add("userUpdateRequest", jsonEntity);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    // When
    ResponseEntity<String> response = restTemplate.exchange(
        "/api/users/" + nonExistentUserId,
        HttpMethod.PATCH,
        requestEntity,
        String.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("사용자 삭제 - 성공")
  void deleteUser_Success() {
    // Given
    UserDto createdUser = createTestUser();

    // When
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/users/" + createdUser.id(),
        HttpMethod.DELETE,
        null,
        Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  @DisplayName("사용자 삭제 - 실패 (존재하지 않는 사용자)")
  void deleteUser_Fail_UserNotFound() {
    // Given
    UUID nonExistentUserId = UUID.randomUUID();

    // When
    ResponseEntity<String> response = restTemplate.exchange(
        "/api/users/" + nonExistentUserId,
        HttpMethod.DELETE,
        null,
        String.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("전체 사용자 조회 - 성공")
  void findAllUsers_Success() {
    // Given
    createTestUser("user1", "user1@example.com");
    createTestUser("user2", "user2@example.com");

    // When
    ResponseEntity<List<UserDto>> response = restTemplate.exchange(
        "/api/users",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDto>>() {
        });

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
  }

  private UserDto createTestUser() {
    return createTestUser("testuser", "test@example.com");
  }

  private UserDto createTestUser(String username, String email) {
    try {
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

      ResponseEntity<UserResponse> response = restTemplate.postForEntity(
          "/api/users",
          requestEntity,
          UserResponse.class);

      UserResponse userResponse = response.getBody();
      return new UserDto(
          userResponse.id(),
          userResponse.createdAt(),
          userResponse.updatedAt(),
          userResponse.username(),
          userResponse.email(),
          null, // profile
          false // online
      );
    } catch (Exception e) {
      throw new RuntimeException("테스트 사용자 생성 실패", e);
    }
  }
}