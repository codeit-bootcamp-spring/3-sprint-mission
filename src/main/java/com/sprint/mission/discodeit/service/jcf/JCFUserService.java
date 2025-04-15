package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    // 생성자: HashMap을 사용해 초기화
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
        data.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User getUserById(UUID userId) {
        return data.get(userId); // ID로 사용자 조회
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values()); // 모든 사용자 목록 반환
    }

    @Override
    public void updateUserName(UUID userId, String newUsername) {
        User user = data.get(userId);
        if (user != null) {
            user.updateUserName(newUsername);
        }
    }

    @Override
    public void updateUserEmail(UUID userId, String newEmail) {
        User user = data.get(userId);
        if (user != null && newEmail != null && !newEmail.isEmpty()) {
            user.updateEmail(newEmail);
        }
    }

    @Override
    public void updateUserPassword(UUID userId, String newPassword) {
        User user = data.get(userId);
        if (user != null) {
            // null 또는 빈 문자열 검사 및 예외 발생
            if (newPassword == null || newPassword.isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 null이거나 빈 문자열일 수 없습니다.");
            }
            user.updatePassword(newPassword);
        }
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id); // 사용자 데이터 삭제
    }
}
