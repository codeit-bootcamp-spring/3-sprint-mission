package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService { // User에 대한 서비스 인터페이스
    User create(String name); // 사용자 생성
    User findById(UUID id); // ID로 사용자 조회
    List<User> findAll(); // 모든 사용자 조회
    User update(UUID id, String newName); // 사용자 이름 수정
    void delete(UUID id); // 사용자 삭제
}
