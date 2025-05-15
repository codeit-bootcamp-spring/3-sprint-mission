package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository {

    User save(User user); // 사용자 저장/업데이트

    Optional<User> findById(UUID userId); // 사용자 조회

    Optional<User> findByUsername(String username); // 사용자 이름 조회

    List<User> findAll(); // 전체 사용자 목록

    void deleteById(UUID userId); // 사용자 삭제

    boolean existsById(UUID id); // 사용자 존재 확인

    boolean existsByEmail(String email); // 이메일 중복 확인
}
