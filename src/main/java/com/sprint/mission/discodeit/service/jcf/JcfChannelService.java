package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JcfChannelService implements ChannelService {
  private final List<Channel> channels = new ArrayList<>();

//  private final JcfUserService userService;
//  구현체 직접 의존 -> 인터페이스 의존
//  //JcfChannelService에서 JcfUserService를 의존성으로 추가하고, 그 안에 저장된 유저를 가져온다.
//  public JcfChannelService(JcfUserService userService) {
//    this.userService = userService;
//  }

  private final UserService userService;
  public JcfChannelService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Channel createChannel(String channelName, User ownerUser) {
    boolean isDuplicate = channels.stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerUser.getId())
            // anyMatch() : 최소한 한 개의 요소가 주어진 조건에 만족하는지 | 하나라도 만족하면 true
            && c.getChannelName().equals(channelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다. 다른 이름을 설정해주세요.");
    }

    List<User> members = new ArrayList<>();
    members.add(ownerUser);
    Channel channel = new Channel(channelName, ownerUser, members);
    channels.add(channel);
    return channel;
  }

  @Override
  public Channel getChannelById(UUID channelId) {
    return channels.stream()
        .filter(e -> e.getId().equals(channelId))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Channel> getAllChannels() {
    return new ArrayList<>(channels);
  }

  @Override
  public void updateChannelName(UUID channelId, String newChannelName) {
    Channel channel = getChannelById(channelId);
    if (channel == null) { // 채널이 존재해야 update가능
      throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
    }

    if (channel.getChannelName().equals(newChannelName)) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다. 다른 이름을 입력해주세요.");
    }

    UUID ownerId = channel.getChannelOwner().getId(); // 채널소유자 기준으로 채널명 중복검사
    boolean isDuplicate = channels.stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(newChannelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("해당 채널명은 이미 사용 중입니다.");
    }
    channel.updateChannelName(newChannelName);
  }

  @Override
  public void deleteChannel(UUID channelId) {
    //channels.remove(getChannelById(channelId));
    channels.removeIf(e -> e.getId().equals(channelId));
  }

  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userService.getUserById(userId);

    if (user == null) { // 유저가 없으면 로그만 남기고 종료
      System.out.println("오류: addMember() 탈퇴한 test02 유저를 채널에 추가 시도 || 존재하지 않은 유저 ID(NULL)입니다. -> " + userId);
      return;
    }
    channel.addChannelUser(user);
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userService.getUserById(userId);
    channel.removeChannelUser(user);
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = getChannelById(channelId);
    return new ArrayList<>(channel.getChannelUsers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channels.removeIf(channel -> channel.getChannelOwner().getId().equals(userId));
  }

 @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channels) {
      channel.getChannelUsers().removeIf(user -> user.getId().equals(userId));
    }
  }

  /*   [refactoring]
  서비스 계층에서 유저나 채널이 null일 수 있는 경우, Optional<T>를 사용하는 편인가요?
  CRUD 모든 메서드에 null 체크를 넣는 건 코드가 장황해질 수 있는데, 보통은 어떻게 처리하나요?

  private Optional<Channel> findChannelById(UUID channelId) {
    return channels.stream()
        .filter(c -> c.getId().equals(channelId))
        .findFirst();
  }

 @Override
  public Channel getChannelById(UUID channelId) {
    return findChannelById(channelId).orElse(null);
  }

  ===================================================================================
  커스텀 예외처리 updateUserName() 에서 null체크 생략 가능
@Override
public User getUserById(UUID id) {
  return data.stream()
      .filter(e -> e.getId().equals(id))
      .findFirst()
      .orElseThrow(() -> new UserNotFoundException(id)); //
}

public void updateUserName(UUID id, String name) {
  User user = getUserById(id); // 못 찾으면 여기서 예외 던짐
//  if (user == null) {
//    throw new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id);
//  }
  user.updateName(name);
}

   */
}
