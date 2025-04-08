package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {

    // Create: 새로운 사용자 생성
    User createUser(String username, String email, String password);

    // Read: 사용자 ID로 단일 사용자 조회
    User getUserById(UUID id);

    // 모든 사용자 조회
    List<User> getAllUsers();

    // Update: 사용자 이름 변경
    void updateUserName(UUID id, String newUsername);

    // Update: 이메일 변경
    void updateUserEmail(UUID id, String newEmail);

    //Update: 비밀번호 변경
    void updateUserPassword(UUID id, String newPassword);

    // Delete: 특정 사용자 삭제
    void deleteUser(UUID id);
}
