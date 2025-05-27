package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BaiscUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 유저는 존재하지 않습니다.");
        }
        if (!userStatusRepository.existsById(userId)) {
            throw new RuntimeException("해당 유저의 정보는 이미 등록되어 있습니다.");
        }

        Instant recentStatusAt = request.recentStatusAt();
        UserStatus userStatus = new UserStatus(recentStatusAt, request.userId());

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 id는 존재하지 않습니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll().stream().toList();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus updatedUserStatus = userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("해당 id는 존재하지 않아 업데이트가 불가능 합니다."));
        updatedUserStatus.update(request.newLastActiveAt());
        return userStatusRepository.save(updatedUserStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus updatedUserStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 id가 존재하지 않아 업데이트가 불가능 합니다."));
        updatedUserStatus.update(request.newLastActiveAt());
        return userStatusRepository.save(updatedUserStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("해당 id는 존재하지 않아 삭제가 불가능합니다.");
        }
        userStatusRepository.deleteById(id);
    }
}
