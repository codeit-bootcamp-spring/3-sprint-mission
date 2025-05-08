package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginReponse;
import com.sprint.mission.discodeit.dto.AuthLogin.AuthLoginRequest;
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

    public AuthLoginReponse login(AuthLoginRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        if (user.getUsername().equals(request.userName()) && user.getPassword().equals(request.password())) {
            return new AuthLoginReponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        } else {
            throw new IllegalArgumentException("유저이름, 비밀번호가 틀립니다. 프로그램을 종료합니다.");
        }
    }
}
