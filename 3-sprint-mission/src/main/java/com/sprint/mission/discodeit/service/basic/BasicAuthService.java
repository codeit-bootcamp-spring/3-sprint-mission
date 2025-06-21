package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto login(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String password = loginRequest.password();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    if (!user.getPassword().equals(password)) {
      throw new IllegalArgumentException("비밀번호가 틀립니다.");
    }

    return userMapper.toDto(user);
  }

}
