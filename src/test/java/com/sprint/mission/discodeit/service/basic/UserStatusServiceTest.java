
package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusMapper userStatusMapper;

    @InjectMocks
    private BasicUserStatusService userStatusService;

    @Test
    @DisplayName("사용자 상태 생성 - 성공")
    void create_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        Instant lastActiveAt = Instant.now();
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

        User user = mock(User.class);
        given(user.getStatus()).willReturn(null);

        UserStatus savedUserStatus = new UserStatus(user, lastActiveAt);
        UserStatusDto expectedDto = new UserStatusDto(UUID.randomUUID(), userId, lastActiveAt);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(savedUserStatus);
        given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

        // When
        UserStatusDto result = userStatusService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userRepository).should().findById(userId);
        then(userStatusRepository).should().save(any(UserStatus.class));
        then(userStatusMapper).should().toDto(any(UserStatus.class));
    }

    @Test
    @DisplayName("사용자 상태 생성 - 사용자를 찾을 수 없어 실패")
    void create_FailWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userStatusService.create(request))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userStatusRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("사용자 상태 생성 - 이미 존재할 때 실패")
    void create_FailWhenUserStatusAlreadyExists() {
        // Given
        UUID userId = UUID.randomUUID();
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());

        User user = mock(User.class);
        UserStatus existingStatus = mock(UserStatus.class);
        given(user.getStatus()).willReturn(existingStatus);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> userStatusService.create(request))
            .isInstanceOf(UserStatusAlreadyExistException.class);

        then(userRepository).should().findById(userId);
        then(userStatusRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("사용자 상태 조회 - 성공")
    void find_Success() {
        // Given
        UUID userStatusId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant lastActiveAt = Instant.now();

        UserStatus userStatus = mock(UserStatus.class);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, lastActiveAt);

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // When
        UserStatusDto result = userStatusService.find(userStatusId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userStatusRepository).should().findById(userStatusId);
        then(userStatusMapper).should().toDto(userStatus);
    }

    @Test
    @DisplayName("사용자 상태 조회 - 상태를 찾을 수 없어 실패")
    void find_FailWhenUserStatusNotFound() {
        // Given
        UUID userStatusId = UUID.randomUUID();

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userStatusService.find(userStatusId))
            .isInstanceOf(UserStatusNotFoundException.class);

        then(userStatusRepository).should().findById(userStatusId);
        then(userStatusMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("전체 사용자 상태 조회 - 성공")
    void findAll_Success() {
        // Given
        UserStatus userStatus1 = mock(UserStatus.class);
        UserStatus userStatus2 = mock(UserStatus.class);
        List<UserStatus> userStatuses = Arrays.asList(userStatus1, userStatus2);

        UserStatusDto dto1 = new UserStatusDto(UUID.randomUUID(), UUID.randomUUID(), Instant.now());
        UserStatusDto dto2 = new UserStatusDto(UUID.randomUUID(), UUID.randomUUID(), Instant.now());
        List<UserStatusDto> expectedDtos = Arrays.asList(dto1, dto2);

        given(userStatusRepository.findAll()).willReturn(userStatuses);
        given(userStatusMapper.toDto(userStatus1)).willReturn(dto1);
        given(userStatusMapper.toDto(userStatus2)).willReturn(dto2);

        // When
        List<UserStatusDto> result = userStatusService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedDtos);
        then(userStatusRepository).should().findAll();
        then(userStatusMapper).should().toDto(userStatus1);
        then(userStatusMapper).should().toDto(userStatus2);
    }

    @Test
    @DisplayName("사용자 상태 수정 - 성공")
    void update_Success() {
        // Given
        UUID userStatusId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

        UserStatus existingUserStatus = mock(UserStatus.class);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, newLastActiveAt);

        given(userStatusRepository.findById(userStatusId)).willReturn(
            Optional.of(existingUserStatus));
        given(userStatusMapper.toDto(existingUserStatus)).willReturn(expectedDto);

        // When
        UserStatusDto result = userStatusService.update(userStatusId, request);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userStatusRepository).should().findById(userStatusId);
        then(existingUserStatus).should().update(newLastActiveAt);
        then(userStatusMapper).should().toDto(existingUserStatus);
    }

    @Test
    @DisplayName("사용자 상태 수정 - 상태를 찾을 수 없어 실패")
    void update_FailWhenUserStatusNotFound() {
        // Given
        UUID userStatusId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userStatusService.update(userStatusId, request))
            .isInstanceOf(UserStatusNotFoundException.class);

        then(userStatusRepository).should().findById(userStatusId);
        then(userStatusMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("사용자 ID로 상태 수정 - 성공")
    void updateByUserId_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();
        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

        UserStatus existingUserStatus = mock(UserStatus.class);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, newLastActiveAt);

        given(userStatusRepository.findByUserId(userId)).willReturn(
            Optional.of(existingUserStatus));
        given(userStatusMapper.toDto(existingUserStatus)).willReturn(expectedDto);

        // When
        UserStatusDto result = userStatusService.updateByUserId(userId, request);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userStatusRepository).should().findByUserId(userId);
        then(existingUserStatus).should().update(newLastActiveAt);
        then(userStatusMapper).should().toDto(existingUserStatus);
    }

    @Test
    @DisplayName("사용자 ID로 상태 수정 - 상태를 찾을 수 없어 실패")
    void updateByUserId_FailWhenUserStatusNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
            .isInstanceOf(UserStatusNotFoundException.class);

        then(userStatusRepository).should().findByUserId(userId);
        then(userStatusMapper).should(never()).toDto(any());
    }

    @Test
    @DisplayName("사용자 상태 삭제 - 성공")
    void delete_Success() {
        // Given
        UUID userStatusId = UUID.randomUUID();

        given(userStatusRepository.existsById(userStatusId)).willReturn(true);

        // When
        userStatusService.delete(userStatusId);

        // Then
        then(userStatusRepository).should().existsById(userStatusId);
        then(userStatusRepository).should().deleteById(userStatusId);
    }

    @Test
    @DisplayName("사용자 상태 삭제 - 상태를 찾을 수 없어 실패")
    void delete_FailWhenUserStatusNotFound() {
        // Given
        UUID userStatusId = UUID.randomUUID();

        given(userStatusRepository.existsById(userStatusId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> userStatusService.delete(userStatusId))
            .isInstanceOf(UserStatusNotFoundException.class);

        then(userStatusRepository).should().existsById(userStatusId);
        then(userStatusRepository).should(never()).deleteById(userStatusId);
    }
}