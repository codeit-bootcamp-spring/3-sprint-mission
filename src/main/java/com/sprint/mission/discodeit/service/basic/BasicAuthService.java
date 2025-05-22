package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
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

  @Override
  public User login(LoginRequest loginRequest) {
    User user = this.userRepository.findByUsername(loginRequest.username()).orElseThrow(
        () -> new NoSuchElementException(
            "User with email " + loginRequest.username() + " not found"));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다.");
    }

    return user;
  }
}
