package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

  private final Map<UUID, User> usersRepository = new HashMap<>();

  @Override
  public User createUser(String email, String name, String password) {
    // 이메일 중복 검사
    validateUserEmail(email);

    User user = User.create(email, name, password);
    usersRepository.put(user.getId(), user);
    return user;
  }

  private void validateUserEmail(String email) {
    if (usersRepository.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return Optional.ofNullable(usersRepository.get(id));
  }

  @Override
  public List<User> searchUsersByName(String name) {
    return usersRepository.values().stream()
        .filter(user -> user.getName().contains(name))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<User> getUserByEmail(String email) {
    return usersRepository.values().stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst(); // 일치하는 결과가 없다면 Optional.empty()
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(usersRepository.values());
  }

  @Override
  public Optional<User> updateUser(UUID id, String name, String password) {
    return Optional.ofNullable(usersRepository.get(id))
        .map(user -> {
          if (name != null) {
            user.updateName(name);
          }
          if (password != null) {
            user.updatePassword(password);
          }
          return user;
        });
  }

  @Override
  public Optional<User> deleteUser(UUID id) {
    return Optional.ofNullable(usersRepository.remove(id));
  }

  public static class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID userId) {
      super("유저를 찾을 수 없음: " + userId);
    }
  }

  public static class UserNotParticipantException extends RuntimeException {

    public UserNotParticipantException() {
      super("채널 참여자가 아닙니다.");
    }
  }
}