package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    Map<UUID, UserStatus> userStatusMap = new HashMap<>(); // userId와 UserStatus를 entry로 가지는 JCF 생성

    UserRepository userRepository;
    UserStatusRepository userStatusRepository;

    public JCFUserStatusRepository(UserRepository userRepository, UserStatusRepository userStatusRepository) {
        this.userStatusMap  = new HashMap<>();
        //
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public UserStatus save(UserStatus userStatus) {

        // 1. User 찾기
        UUID userId = userStatus.getUserId();

        // 2. <User, UserStatus>를 JCF에 저장
        userStatusMap.put(userId, userStatus);

        return userStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {

        return Optional.ofNullable(userStatusMap.get(userId));
    }

    @Override
    public Optional<UserStatus> find(UUID id) {

        // TODO Map에 저장되어 있는 UserStatus의 id로 UserStatus를 불러와야함
        // 1. userStatusMap의 values를 List로 변환
        UserStatus userStatus = userStatusMap.values()
                .stream()
                .filter(status -> status.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("UserStatus with that id not found"));

        return Optional.ofNullable(userStatus);
    }

    @Override
    public List<UserStatus> findAll() {

        List<UserStatus> userStatusList = userStatusMap.values()
                .stream()
                .toList();

        return userStatusList;
    }

    @Override
    public boolean existsByUserId(UUID userId) {

        // 1. userStatusMap에서 userId에 해당하는 UserStatus가 존재하는지 확인
        boolean exists = userStatusMap.values()
                .stream()
                .anyMatch(status -> status.getUserId().equals(userId));

        return exists;
    }

    @Override
    public void delete(UUID userId) {

        userStatusMap.remove(userId);
    }
}
