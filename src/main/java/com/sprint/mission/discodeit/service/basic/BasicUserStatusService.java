package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.entity.User;
import com.sprint.mission.discodeit.dto.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest userStatusCreateRequest) {
        UUID userId = userStatusCreateRequest.getUserId();

        User user = userRepository.loadById(userId);

        if (user == null) {
            throw new IllegalArgumentException("[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userId + ")");
        }

        if (user.getUserStatusId() != null) {
            throw new IllegalArgumentException("[UserStatus] 이미 관련된 객체가 존재합니다. (userId=" + userId + ")");
        }

        UserStatus userStatus =  UserStatus.of(userId);
        userStatusRepository.save(userStatus);

        return userStatus;
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.loadById(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.loadAll();
    }

    @Override
    public UserStatus updateByStatusId(UserStatusUpdateRequest userStatusUpdateRequest) {
        if (userRepository.loadById(userStatusUpdateRequest.getUserId()) == null) {
            throw new IllegalArgumentException("[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userStatusUpdateRequest.getUserId() + ")");
        }

        UserStatus status = userStatusRepository.loadById(userStatusUpdateRequest.getUserStatusId());
        status.update();

        userStatusRepository.save(status);
        return status;
    }


    @Override
    public UserStatus updateByUserId(UUID userId) {
        if (userRepository.loadById(userId) == null) {
            throw new IllegalArgumentException(
                    "[UserStatus] 존재하지 않은 사용자입니다. (userId=" + userId + ")"
            );
        }

        UserStatus status = userStatusRepository.loadById(userId);
        status.update();
        userStatusRepository.save(status);

        return status;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
