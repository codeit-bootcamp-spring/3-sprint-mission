package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

// @ConditionalOnProperty : Spring Boot에서 특정 프로퍼티의 값에 따라 Bean 생성 여부 제어
// 해당 이름의 타입의 값을 jcf로 설정했다면 Bean 생성
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        // 값이 존재하면 해당 ID 반환, 없다면 Null 반환
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.findAll().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID userId) {
        return this.data.containsKey(userId);
    }

    @Override
    public void deleteById(UUID userId) {
        this.data.remove(userId);
    }
}
