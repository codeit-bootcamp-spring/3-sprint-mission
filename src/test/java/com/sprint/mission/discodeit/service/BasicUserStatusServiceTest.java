package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusMapper userStatusMapper;
    @InjectMocks
    private BasicUserStatusService userStatusService;

    @Test
    @DisplayName("사용자 상태 생성 성공")
    void shouldCreateUserStatusSuccessfully() {
        UUID userId = UUID.randomUUID();
        Instant lastActiveAt = Instant.now();
        User user = new User("username", "email", "password", null);
        UserStatus userStatus = new UserStatus(user, lastActiveAt);
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(userStatusRepository.save(any())).willReturn(userStatus);
        given(userStatusMapper.toResponse(any())).willReturn(
            new UserStatusResponse(userStatus.getId(), userId, lastActiveAt));

        UserStatusResponse response = userStatusService.create(request);

        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자 미발견 시 상태 생성 실패")
    void shouldThrowUserNotFoundException_whenUserNotFoundForCreate() {
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(
            () -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("중복 사용자 상태 생성 시 예외 발생")
    void shouldThrowUserStatusAlreadyExistsException_whenDuplicateStatusForCreate() {
        UUID userId = UUID.randomUUID();
        User user = new User("u", "e", "p", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(
            Optional.of(new UserStatus(user, Instant.now())));

        assertThatThrownBy(
            () -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())))
            .isInstanceOf(UserStatusAlreadyExistsException.class);
    }

    @Test
    @DisplayName("사용자 상태 조회 성공")
    void shouldFindUserStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = new User("username", "email", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userStatusRepository.findById(id)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toResponse(userStatus)).willReturn(
            new UserStatusResponse(id, user.getId(), userStatus.getLastActiveAt()));

        UserStatusResponse response = userStatusService.find(id);
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("사용자 상태 수정 성공")
    void shouldUpdateUserStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        Instant newTime = Instant.now();
        User user = new User("username", "email", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userStatusRepository.findById(id)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toResponse(userStatus)).willReturn(
            new UserStatusResponse(id, user.getId(), newTime));

        UserStatusResponse response = userStatusService.update(id,
            new UserStatusUpdateRequest(newTime));
        assertThat(response.lastActiveAt()).isEqualTo(newTime);
    }

    @Test
    @DisplayName("사용자 상태 미발견 시 수정 실패")
    void shouldThrowUserStatusNotFoundException_whenUserStatusNotFoundForUpdate() {
        UUID userId = UUID.randomUUID();
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userStatusService.updateByUserId(userId,
            new UserStatusUpdateRequest(Instant.now())))
            .isInstanceOf(UserStatusNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 상태 삭제 성공")
    void shouldDeleteUserStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = new User("username", "email", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userStatusRepository.findById(id)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toResponse(userStatus)).willReturn(
            new UserStatusResponse(id, user.getId(), userStatus.getLastActiveAt()));

        UserStatusResponse response = userStatusService.delete(id);

        then(userStatusRepository).should().deleteById(id);
        assertThat(response.id()).isEqualTo(id);
    }
}
