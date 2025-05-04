package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
  private static final String FILE_PATH = "users.ser";
  private final Map<UUID, User> userData;
  private ChannelService channelService;
  private final UserStatusRepository userStatusRepository;

  public FileUserService(UserStatusRepository userStatusRepository) {
    this.userStatusRepository = userStatusRepository;
    this.userData = loadUserData(); // 파일에서 데이터 로드
  }

  public void setChannelService(ChannelService channelService) {
    this.channelService = channelService;
  }

  private void saveUserData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(userData); // 데이터를 파일에 저장
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<UUID, User> loadUserData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>(); // 파일이 없으면 빈 Map 반환
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject(); // 파일에서 데이터 읽기
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>(); // 오류가 발생하면 빈 Map 반환
    }
  }

  @Override
  public User createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }
    User user = new User(username, email, userCreateRequest.password());
    userData.put(user.getId(), user);
    saveUserData();
    return user;
  }

  @Override
  public Optional<UserDto> find(UUID id) {
    return Optional.ofNullable(userData.get(id))
        .map(user -> {
          boolean hasProfileImage = user.getProfileId() != null;
          boolean isOnline = userStatusRepository.find(user.getId())
              .map(status -> status.checkUserConnect())
              .orElse(false);
          return new UserDto(user, hasProfileImage, isOnline);
        });
  }

  @Override
  public List<UserDto> findAll() {
    return userData.values().stream()
        .map(user -> {
          boolean hasProfileImage = user.getProfileId() != null;
          boolean isOnline = userStatusRepository.find(user.getId())
              .map(status -> status.checkUserConnect())
              .orElse(false);
          return new UserDto(user, hasProfileImage, isOnline);
        })
        .collect(Collectors.toList());
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
    return null;
  }

  public void updateUserName(UUID id, String name) {
//    User user = find(id)
//        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));
//    user.updateName(name);
//    saveUserData();
  }

  public void updateUserEmail(UUID id, String email) {
//    User user = find(id)
//        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));
//
//    if (user.getEmail().equalsIgnoreCase(email)) {
//      throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
//    }
//
//    if (isEmailDuplicate(email)) {
//      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
//    }
//
//    user.updateEmail(email);
//    saveUserData();
  }

  @Override
  public void delete(UUID id) {
//    User user = find(id)
//        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));
//
//    if (channelService != null) {
//      channelService.deleteChannelsCreatedByUser(id);
//      channelService.removeUserFromAllChannels(id);
//    }
//
//    userData.remove(id);
//    saveUserData();
  }

  private boolean isEmailDuplicate(String email) {
    return userData.values().stream()
        .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)); // 이메일 중복 검사
  }
}
