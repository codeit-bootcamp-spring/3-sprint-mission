package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
  private final UserRepository userRepository;

  public BasicUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User create(String username) {
    // Create a new user entity
    User user = new User(username);
    // Save user using repository
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
  public void update(UUID id, String newUsername) {
    User user = userRepository.findById(id);
    if (user != null) {
      user.updateUsername(newUsername);
      userRepository.save(user);
    }
  }

  @Override
  public void delete(UUID id) {
    userRepository.delete(id);
  }
}
