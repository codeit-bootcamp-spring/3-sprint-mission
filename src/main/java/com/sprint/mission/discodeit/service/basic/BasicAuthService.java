package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserDto login(LoginRequest loginRequest) {
        Optional<User> matchedUser = userRepository.findAll().stream()
                .filter(user -> Objects.equals(user.getUsername(), loginRequest.username()) &&
                        Objects.equals(user.getPassword(), loginRequest.password()))
                .findFirst();

        User user = matchedUser.orElseThrow(() ->
                new NoSuchElementException("Invalid username or password.")
        );

        return new UserDto(user, false);
    }
}
