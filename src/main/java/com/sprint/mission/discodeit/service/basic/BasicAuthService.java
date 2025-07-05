package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponse login(String username, String password) {
    User user = userRepository.findByUsername(username).orElseThrow(
        () -> new UserNotFoundException(username));

    if (!user.getPassword().equals(password)) {
      throw new InvalidPasswordException();
    }

    user.getUserStatus().updateLastActiveAt();
    userRepository.save(user);

    return userMapper.toResponse(user);
  }
}
