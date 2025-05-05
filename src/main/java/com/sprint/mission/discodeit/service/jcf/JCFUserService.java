package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JCFUserService implements UserService {

  private final UserRepository userRepository;

  public JCFUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserResponse create(UserCreateRequest request) {
    throw new UnsupportedOperationException(
        "JCFUserService에서는 create(UserCreateRequest)를 지원하지 않습니다.");
  }

  @Override
  public User create(String username) {
    User user = new User(username);
    userRepository.save(user);
    return user;
  }

  @Override
  public UserResponse findById(UUID id) {
    throw new UnsupportedOperationException("JCFUserService에서는 findById(UUID)를 지원하지 않습니다.");
  }

  @Override
  public List<UserResponse> findAll() {
    throw new UnsupportedOperationException("JCFUserService에서는 findAll()을 지원하지 않습니다.");
  }

  @Override
  public User update(UUID id, String newUsername) {
    User user = userRepository.findById(id);
    if (user != null) {
      user.updateUsername(newUsername);
      userRepository.save(user);
    }
    return user;
  }

  @Override
  public UserResponse update(UserUpdateRequest request) {
    throw new UnsupportedOperationException(
        "JCFUserService에서는 update(UserUpdateRequest)를 지원하지 않습니다.");
  }

  @Override
  public User delete(UUID id) {
    User user = userRepository.findById(id);
    if (user != null) {
      userRepository.delete(id);
    }
    return user;
  }
}
