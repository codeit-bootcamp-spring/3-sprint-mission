package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> userStatuses;

    public JCFUserStatusRepository() {
        this.userStatuses = new HashMap<>();
    }

    @Override
    public UserStatus createUserStatus(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        UserStatus status = new UserStatus(userStatusCreateRequestDto.getUserId(), userStatusCreateRequestDto.getNowTime());
        if (userStatuses.containsKey(status.getId())) {
            throw new IllegalArgumentException("데이터가 이미 존재합니다.");
        }

        userStatuses.put(status.getId(), status);
        return status;
    }

    @Override
    public UserStatus findStatusById(UUID statusId) {
        if (!userStatuses.containsKey(statusId)) {
            throw new NoSuchElementException("존재하지 않는 데이터입니다.");
        }
        return userStatuses.get(statusId);
    }

    @Override
    public List<UserStatus> findAllStatus() {
        return userStatuses.values().stream().toList();
    }

    @Override
    public boolean updateUserStatus(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatuses.get(userStatusUpdateRequestDto.getUserStatusId());
        if (userStatus == null) {
            throw new NoSuchElementException("존재하지 않는 데이터입니다.");
        }

        userStatus.setLastActivityAt(userStatusUpdateRequestDto.getNowTime());

        return true;
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {

        for (UserStatus userStatus : userStatuses.values()) {
            if (userStatus.getUserId().equals(userId)) {
                userStatus.setLastActivityAt(Instant.now());
                return userStatus;
            }
        }
        throw new NoSuchElementException("존재하지 않는 데이터입니다.");
    }

    @Override
    public boolean deleteUserStatus(UUID statusId) {
        if (userStatuses.containsKey(statusId)) {
            userStatuses.remove(statusId);
            return true;
        }
        return false;
    }
}
