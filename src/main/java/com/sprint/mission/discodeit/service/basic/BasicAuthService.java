package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.User.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public UserDto login(LoginRequest loginRequest) {
    User user = this.userRepository.findByUsername(loginRequest.username()).orElseThrow(
        () -> new UserNotFoundException(loginRequest.username()));

    if (!user.getPassword().equals(loginRequest.password())) {
      throw new InvalidCredentialsException(loginRequest.username(), loginRequest.password());
    }

    return userMapper.toDto(user);
  }
}
