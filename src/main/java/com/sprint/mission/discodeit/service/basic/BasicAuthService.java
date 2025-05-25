package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;


    @Override
    public User Login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        User user = userRepository.findAll()
            .stream()
            .filter(user1 -> user1.getUsername().equals(username)).findFirst().orElse(null);
        if(user == null){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return user;
    }
}
