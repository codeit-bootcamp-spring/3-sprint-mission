package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public AuthLoginReponse login(LoginRequest request) {

        User user = userRepository.findByUserName(request.username())
                .orElseThrow(() -> new NoSuchElementException("해당하는 유저는 존재하지 않습니다."));

        if (user.getUsername().equals(request.username()) && user.getPassword().equals(request.password())) {
            return AuthLoginReponse.builder()
                    .id(user.getId())
                    .userName(user.getUsername())
                    .email(user.getEmail())
                    .build();
        } else {
            throw new IllegalArgumentException("유저이름, 비밀번호가 틀립니다. ");
        }
    }
}
