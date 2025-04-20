package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private ChannelService channelService;

  // 생성자를 통해 Repository 주입
  public BasicUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // 순환 참조 방지를 위한 setter 주입
  public void setChannelService(ChannelService channelService) {
    this.channelService = channelService;
  }

  @Override
  public User createUser(String username, String email) {
    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }
    User user = new User(username, email);
    userRepository.save(user);
    return user;
  }

  @Override
  public Optional<User> getUserById(UUID id) {
    return userRepository.getUserById(id);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public void updateUserName(UUID id, String name) {
    User user = userRepository.getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    user.updateName(name);
    userRepository.update(user);
  }

  public void updateUserEmail(UUID id, String email) {
    User user = userRepository.getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    if (user.getEmail().equalsIgnoreCase(email)) {
      throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
    }

    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }

    user.updateEmail(email);
    userRepository.update(user);
  }

  @Override
  public void deleteUser(UUID id) {
    User user = userRepository.getUserById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

    if (channelService != null) {
      channelService.deleteChannelsCreatedByUser(id);
      channelService.removeUserFromAllChannels(id);
    }

    userRepository.delete(id);
  }

  private boolean isEmailDuplicate(String email) {
    return userRepository.getAllUsers().stream()
        .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
  }
}
