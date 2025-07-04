package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 상태 서비스 단위 테스트")
public class UserStatusServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private UserStatusMapper userStatusMapper;

    @InjectMocks private BasicUserStatusService userStatusService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("테스트유저", "test@email.com", "0000", null);
    }

    @Test
    @DisplayName("정상적으로 UserStatus를 생성할 수 있다")
    void createUserStatusSuccess() {
        // given
        UUID userId = user.getId();
        Instant now = Instant.now();
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(userStatusRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        UserStatus result = userStatusService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLastActiveAt()).isEqualTo(now);
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("이미 존재하는 UserStatus가 있는 경우 예외 발생")
    void createUserStatusDuplicate() throws NoSuchFieldException, IllegalAccessException {
        // given
        UUID userId = user.getId();
        Instant now = Instant.now();
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);

        UserStatus existingStatus = new UserStatus(user, now);

        // BaseUpdatableEntity의 id 필드에 접근해서 UUID 설정
        Field idField = UserStatus.class.getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        UUID statusId = UUID.randomUUID();
        idField.set(existingStatus, statusId);

        user.setUserStatus(existingStatus);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(existingStatus));

        // when & then
        assertThatThrownBy(() -> userStatusService.create(request))
                .isInstanceOf(DuplicateUserStatusException.class);
    }

    @Test
    @DisplayName("ID로 UserStatus 조회 - 존재하지 않으면 예외 발생")
    void findUserStatusNotFound() {
        // given
        UUID statusId = UUID.randomUUID();
        given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStatusService.find(statusId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("모든 UserStatus 조회")
    void findAllUserStatuses() {
        // given
        List<UserStatus> statuses = List.of(
                new UserStatus(user, Instant.now())
        );
        given(userStatusRepository.findAll()).willReturn(statuses);

        // when
        List<UserStatus> result = userStatusService.findAll();

        // then
        assertThat(result).hasSize(1);
        verify(userStatusRepository).findAll();
    }

    @Test
    @DisplayName("UserStatus ID로 업데이트")
    void updateUserStatus() {
        // given
        UUID userId = user.getId();
        UUID statusId = UUID.randomUUID();
        Instant updated = Instant.now();
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(
                new UserStatusDto(UUID.randomUUID(), userId, updated)
        );

        // when
        UserStatusDto dto = userStatusService.update(statusId, new UserStatusUpdateRequest(updated));

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.userId()).isEqualTo(userId);
        verify(userStatusRepository).findById(statusId);
    }

    @Test
    @DisplayName("UserId로 UserStatus 업데이트")
    void updateByUserId() {
        // given
        UUID userId = user.getId();
        Instant updated = Instant.now();
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(
                new UserStatusDto(UUID.randomUUID(), userId, updated)
        );

        // when
        UserStatusDto dto = userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(updated));

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID 삭제 시 예외 발생")
    void deleteNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(userStatusRepository.existsById(id)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userStatusService.delete(id))
                .isInstanceOf(UserStatusNotFoundException.class);
    }

    @Test
    @DisplayName("정상적으로 UserStatus 삭제")
    void deleteSuccess() {
        // given
        UUID id = UUID.randomUUID();
        given(userStatusRepository.existsById(id)).willReturn(true);

        // when
        userStatusService.delete(id);

        // then
        verify(userStatusRepository).deleteById(id);
    }
}
