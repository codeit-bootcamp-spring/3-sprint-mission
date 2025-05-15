package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 사용자 상태가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(userId, request.lastOnlineAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태를 찾을 수 없습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태를 찾을 수 없습니다."));
        userStatus.update(request.lastOnlineAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태를 찾을 수 없습니다."));
        userStatus.update(request.lastOnlineAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("해당 사용자 상태를 찾을 수 없습니다.");
        }
        userStatusRepository.deleteById(userStatusId);
    }
}