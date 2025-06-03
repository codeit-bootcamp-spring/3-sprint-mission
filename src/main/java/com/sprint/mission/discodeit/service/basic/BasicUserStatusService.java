package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("userStatus가 이미 존재합니다.");
        }

        UserStatus status = new UserStatus(user, request.lastActiveAt());
        UserStatus newUserStatus = userStatusRepository.save(status);

        return userStatusMapper.toDto(newUserStatus);
    }

    @Override
    public UserStatusDto findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("userStatus가 존재하지 않음"));
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {

        UserStatus status = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new NoSuchElementException("userStatus가 존재하지 않음"));

        status.update(request.newLastActiveAt());
        UserStatus updatedUserStatus = userStatusRepository.save(status);
        return userStatusMapper.toDto(updatedUserStatus);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {

        UserStatus status = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("userStatus가 존재하지 않음"));

        status.update(request.newLastActiveAt());
        UserStatus updatedUserStatus = userStatusRepository.save(status);
        return userStatusMapper.toDto(updatedUserStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (userStatusRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("userStatus가 존재하지 않음");
        }
        userStatusRepository.deleteById(id);
    }
}

