package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

// User 저장소를 위한 공통 인터페이스
public interface UserRepository {
    void save(User user);                  // 사용자 저장
    User findById(UUID id);                // ID로 사용자 조회
    List<User> findAll();                  // 전체 사용자 조회
    void update(User user);                // 사용자 정보 수정
    void delete(UUID id);                  // 사용자 삭제
}