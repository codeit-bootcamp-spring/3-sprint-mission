package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JcfUserService implements UserService {
  private final List<User> data = new ArrayList<>();
  /*
      JcfUserService → JcfChannelService에 의존
      유저가 탈퇴할 때 그 유저가 만든 채널도 삭제해야한다. 속한 채널에서도 그 유저가 사라져야 한다.
      JcfChannelService → JcfUserService에 의존
      사용자 ID로 User 객체 가져오기 위해
      순환참조 발생
  */
  //private JcfChannelService channelService;
  private ChannelService channelService;
  public void setChannelService(ChannelService channelService) {
    this.channelService = channelService;
  }

  @Override
  public User createUser(String username, String email) {
    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }
    User user = new User(username, email);
    data.add(user);
    return user;
  }

  @Override
  public User getUserById(UUID id) {
    return data.stream()
        .filter(e -> e.getId().equals(id))
        .findFirst() // UUID는 유일하므로 첫 번째 결과 반환. 만약 비어있으면 Optional.empty()를 반환
        .orElse(null); // 유저가 없으면 null 반환
    //  .orElseThrow(() -> new IllegalArgumentException("조회할 User를 찾지 못했습니다."));
  /*
    //직관적인 foreach문
    for (User user : users) {
      if (user.getId().equals(id)) {
        return user;
      }
    }
    throw new IllegalArgumentException("조회할 User를 찾지 못했습니다.");
  */
  }

  @Override
  public List<User> getAllUsers() {
    return new ArrayList<>(data);
  }

  public void updateUserName(UUID id, String name) {
    User user = getUserById(id);
    if (user == null) {
      throw new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id);
    }
    user.updateName(name);
  }

  public void updateUserEmail(UUID id, String email) {
    User user = getUserById(id);
    if (user == null) {
      throw new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id);
    }

    if (user.getEmail().equalsIgnoreCase(email)) {
      throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
    }

    if (isEmailDuplicate(email)) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }

    user.updateEmail(email);
  }

  @Override
  public void deleteUser(UUID id) { // 유저가 삭제되면, 그 유저가 만든 채널도 함께 삭제되도록 연동이 필요
    User user = getUserById(id);
    if (user == null) {
      throw new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id);
    }

    if (channelService != null) {
      channelService.deleteChannelsCreatedByUser(id);   // 유저가 만든 채널 삭제
      channelService.removeUserFromAllChannels(id);     // 유저가 참여한 채널에서 탈퇴
    }

    data.remove(user);
  }

  private boolean isEmailDuplicate(String email) {
    return data.stream()
        .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
  }
  // 시간복잡도
  // Create - List.add() -> O(1) ~ O(N)
  // Read - getUserById() ... filter() -> O(N)   &   getAllUsers() -> new ArrayList<> 리스트 복사 O(N)
  // Update - getUserById() ... filter() -> O(N)
  // Delete - getUserById() ... filter() -> O(N)
}
