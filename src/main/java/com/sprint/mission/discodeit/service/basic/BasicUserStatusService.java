package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 존재하지 않습니다."));

        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("해당 유저의 상태는 이미 등록되어 있습니다.");
        }

        UserStatus userStatus = new UserStatus(user, request.recentStatusAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저 상태가 존재하지 않습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 상태가 존재하지 않습니다."));

        userStatus.update(request.newLastActiveAt());
        return userStatus;
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        return update(userId, request);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 상태가 존재하지 않습니다."));

        userStatusRepository.delete(userStatus);
    }
}