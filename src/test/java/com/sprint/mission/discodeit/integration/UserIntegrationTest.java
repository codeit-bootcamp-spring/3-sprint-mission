package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User 엔드포인트의 통합 테스트 클래스입니다.
 * <p>
 * 실제 DB와 연동하여 전체 애플리케이션 레이어를 검증합니다.
 * - 사용자 생성, 수정, 삭제, 전체 조회 등 다양한 시나리오를 검증합니다.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = DiscodeitApplication.class
)
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    /**
     * [성공] 사용자를 DB에 저장하고 조회할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 사용자 DB 저장 및 조회")
    void shouldCreateUserAndRetrieveFromDatabase() {
        // given: 사용자 생성 요청 데이터
        UserCreateRequest createRequest = new UserCreateRequest(
            "testuser_create", "test_create@test.com", "password123"
        );

        // multipart/form-data로 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userCreateRequest", createRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // when: 사용자 생성 API 호출
        ResponseEntity<UserDto> response = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            UserDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testuser_create");
        assertThat(response.getBody().email()).isEqualTo("test_create@test.com");

        // DB에서 실제 저장되었는지 확인
        User savedUser = userRepository.findByUsername("testuser_create").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test_create@test.com");
    }

    /**
     * [성공] 사용자를 DB에서 수정할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 사용자 DB 수정")
    void shouldUpdateUserInDatabase() {
        // given: 기존 사용자를 DB에 저장
        User existingUser = userRepository.save(new User("olduser_update", "old_update@test.com", "password123", null));
        
        UserUpdateRequest updateRequest = new UserUpdateRequest(
            "newuser_update", "new_update@test.com", "newpassword123"
        );

        // multipart/form-data로 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userUpdateRequest", updateRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // when: 사용자 수정 API 호출
        ResponseEntity<UserDto> response = restTemplate.exchange(
            "/api/users/" + existingUser.getId(),
            HttpMethod.PATCH,
            new HttpEntity<>(body, headers),
            UserDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("newuser_update");
        assertThat(response.getBody().email()).isEqualTo("new_update@test.com");

        // DB에서 실제 수정되었는지 확인
        User updatedUser = userRepository.findById(existingUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getUsername()).isEqualTo("newuser_update");
    }

    /**
     * [성공] 사용자를 DB에서 삭제할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 사용자 DB 삭제")
    void shouldDeleteUserFromDatabase() {
        // given: 사용자를 DB에 저장
        User user = userRepository.save(new User("deleteuser_delete", "delete_delete@test.com", "password123", null));
        UUID userId = user.getId();

        // when: 사용자 삭제 API 호출
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/users/" + userId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // then: 응답 상태코드 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DB에서 실제 삭제되었는지 확인
        User deletedUser = userRepository.findById(userId).orElse(null);
        assertThat(deletedUser).isNull();
    }

    /**
     * [성공] 전체 사용자 목록을 DB에서 조회할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 전체 사용자 목록 DB 조회")
    void shouldRetrieveAllUsersFromDatabase() {
        // given: 여러 사용자를 API로 생성
        for (int i = 1; i <= 3; i++) {
            UserCreateRequest createRequest = new UserCreateRequest(
                "user" + i + "_list", "user" + i + "_list@test.com", "password" + i
            );
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("userCreateRequest", createRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            restTemplate.exchange(
                "/api/users",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                UserDto.class
            );
        }

        // when: 사용자 목록 조회 API 호출
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
            "/api/users",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserDto>>() {}
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(3);

        // 저장한 사용자들이 응답에 포함되어 있는지 확인
        List<String> usernames = response.getBody().stream()
            .map(UserDto::username)
            .toList();
        assertThat(usernames).contains("user1_list", "user2_list", "user3_list");
    }
} 