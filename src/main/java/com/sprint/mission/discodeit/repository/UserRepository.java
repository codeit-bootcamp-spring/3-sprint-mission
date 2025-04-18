package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // 전체 데이터 저장
    public void saveAll();
    // 특정 채널 데이터 추가 후 저장
    public void save(User user);
    // 데이터 삭제
    public void delete(UUID id);
    // 데이터 전체 조회
    public Map<UUID, User> readAll();
    // 데이터 단건 조회(id)
    public Optional<User> readById(UUID id);
    // 데이터 단건 조회(userId)
    public Optional<User> readByUserId(String userId);
}
