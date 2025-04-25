package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileUserService implements UserService {

  private final FileUserRepository userRepository;

  public FileUserService(FileUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public static FileUserService from(String filePath) {
    return new FileUserService(FileUserRepository.from(filePath));
  }

  public static FileUserService createDefault() {
    return new FileUserService(FileUserRepository.createDefault());
  }

  @Override
  public User createUser(String email, String name, String password) {
    validateUserEmail(email);
    User user = User.create(email, name, password);
    return userRepository.save(user);
  }

  private void validateUserEmail(String email) {
    userRepository.findByEmail(email).ifPresent(user -> {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    });
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public List<User> searchUsersByName(String name) {
    return userRepository.findByNameContains(name);
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> updateUser(UUID id, String name, String password) {
    return userRepository.findById(id)
        .map(user -> {
          if (name != null) {
            user.updateName(name);
          }
          if (password != null) {
            user.updatePassword(password);
          }
          return userRepository.save(user);
        });
  }

  @Override
  public Optional<User> deleteUser(UUID id) {
    Optional<User> user = userRepository.findById(id);
    user.ifPresent(u -> userRepository.deleteById(id));
    return user;
  }
}
