package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> userStatusMap = new LinkedHashMap<>();


    // 저장
    @Override
    public UserStatus saveUserStatus(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    // 아이디로 조회
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.userStatusMap.get(id));
    }

    // 유저 아이디로 조회
    @Override
    public Optional<UserStatus> findStatus(UUID userId) {
        return userStatusMap.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    // 전체 조회
    @Override
    public List<UserStatus> findAllStatus() {
        return new ArrayList<>(userStatusMap.values());
    }

    // 수정
    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return true;
    }

    // 아이디로 삭제
    @Override
    public void deleteById(UUID id) {
        userStatusMap.remove(id);
    }

    // 삭제
    @Override
    public void deleteUserStatus(UUID userId) {
        findStatus(userId)
                .ifPresent(userStatus -> deleteUserStatus(userStatus.getId()));
    }
}
