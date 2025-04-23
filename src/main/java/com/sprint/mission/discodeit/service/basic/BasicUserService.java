package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;


  @Override
  public User create(String username) {
    User user = new User(username);
    userRepository.save(user);
    return user;
  }

  @Override
  public User findById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
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
  public User delete(UUID id) {
    User user = userRepository.findById(id);
    if (user != null) {
      userRepository.delete(id);
    }
    return user;
  }
}
