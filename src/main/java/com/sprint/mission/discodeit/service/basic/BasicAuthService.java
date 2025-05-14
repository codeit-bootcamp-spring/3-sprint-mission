package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.DTO.Request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User Login(LoginRequest loginRequest) {
        String username = loginRequest.userName();
        String password = loginRequest.password();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username not found"));

        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("Wrong password");
        }
        return user;
    }
}
