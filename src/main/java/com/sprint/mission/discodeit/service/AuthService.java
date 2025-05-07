package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.LoginRequest;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;

public class AuthService {

    UserRepository userRepository;

    public User login(LoginRequest loginRequest) {

        List<User> userList = userRepository.findAll()
                .stream()
                .toList();

        for (User user : userList) { // O(n) 시간 복잡도
            if (user.getUsername().equals(loginRequest.username()) || user.getPassword().equals(loginRequest.password())) {
                return user;
            }
        }

        throw new IllegalArgumentException("로그인에 실패했습니다. 사용자 이름 또는 비밀번호가 올바르지 않거나 정보가 없습니다.");
    }
}
