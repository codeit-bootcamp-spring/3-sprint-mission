package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    User user = this.userRepository.findByUsername(loginRequest.username()).orElseThrow(
        () -> new NoSuchElementException(
            "User with email " + loginRequest.username() + " not found"));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다.");
    }

    return userMapper.toDto(user);
  }
}
