package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginDTO) {
        String username = loginDTO.username();
        String password = loginDTO.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }

        return user;
    }

}
