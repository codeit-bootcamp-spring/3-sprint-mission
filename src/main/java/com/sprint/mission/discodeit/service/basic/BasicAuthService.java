package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        // UserName 무결성 검사
        User user = userRepository.findAll()
                .stream()
                .filter(u -> u.getUserName().equals(loginRequest.getUserName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("검색하신 [" + loginRequest.getUserName() + "] 은 존재하지 않는 사용자 입니다"));

        // Password 무결성 검사
        if (!user.getPwd().equals(loginRequest.getPwd())) {
            // 보안성을 위해 안내문만 출력
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        return user;
    }
}
