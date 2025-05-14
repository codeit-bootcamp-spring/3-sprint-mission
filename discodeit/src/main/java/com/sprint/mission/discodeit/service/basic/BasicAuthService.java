package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.user.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    public final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest){
        // 일치하는 유저 이름이 있는지 확인하는 파트
        User user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new NoSuchElementException("User not found with username " + loginRequest.username()));

        // 일치하는 password인지 확인하고 잘못된 패스워드면 예외 발생
        if(!user.getPassword().equals(loginRequest.password())){
            throw new IllegalArgumentException("잘못된 password!");
        }

        return user;
    }
}
