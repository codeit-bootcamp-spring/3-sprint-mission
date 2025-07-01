package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw UserStatusAlreadyExistException.withUserId(userId);
        }

        UserStatus status = new UserStatus(user, request.lastActiveAt());
        UserStatus newUserStatus = userStatusRepository.save(status);

        return userStatusMapper.toDto(newUserStatus);
    }

    @Override
    public UserStatusDto findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(() -> UserStatusNotFoundException.withId(id));
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {

        UserStatus status = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> UserStatusNotFoundException.withId(userStatusId));

        status.update(request.newLastActiveAt());
        UserStatus updatedUserStatus = userStatusRepository.save(status);
        return userStatusMapper.toDto(updatedUserStatus);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {

        UserStatus status = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> UserStatusNotFoundException.withUserId(userId));

        status.update(request.newLastActiveAt());
        UserStatus updatedUserStatus = userStatusRepository.save(status);
        return userStatusMapper.toDto(updatedUserStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new DiscodeitException(
                ErrorCode.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", id)
            );
        }
        userStatusRepository.deleteById(id);
    }
}