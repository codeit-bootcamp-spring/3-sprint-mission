package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
  private static final String FILE_PATH = "channels.ser";
  private final Map<UUID, Channel> channelData;

  public FileChannelRepository() {
    this.channelData = loadChannelData();
  }

  // 직렬화된 파일에서 채널 데이터를 불러옴
  private Map<UUID, Channel> loadChannelData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>();
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Channel>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  // 채널 데이터를 파일로 저장
  private void saveChannelData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(channelData);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 1. 채널 생성
  @Override
  public Channel save(Channel channel) {
    channelData.put(channel.getId(), channel);
    saveChannelData();
    return channel;
  }

  // 2. 채널 단건 조회
  @Override
  public Optional<Channel> getChannelById(UUID channelId) {
    return Optional.ofNullable(channelData.get(channelId));
  }

  // 3. 채널 전체 조회
  @Override
  public List<Channel> getAllChannels() {
    return new ArrayList<>(channelData.values());
  }

  // 4. 채널 수정 (채널 이름 등 변경사항 반영)
  @Override
  public void update(Channel channel) {
    if (!channelData.containsKey(channel.getId())) {
      throw new IllegalArgumentException("해당 채널이 존재하지 않습니다: " + channel.getId());
    }
    channelData.put(channel.getId(), channel); // 기존 채널 덮어쓰기
    saveChannelData();
  }

  // 5. 채널 삭제
  @Override
  public void delete(UUID channelId) {
    channelData.remove(channelId);
    saveChannelData();
  }

  // 6. 유저가 만든 채널 전체 삭제
  @Override
  public void deleteByOwnerId(UUID userId) {
    channelData.entrySet().removeIf(entry -> entry.getValue().getChannelOwner().getId().equals(userId));
    saveChannelData();
  }

  // 7. 유저가 참여 중인 모든 채널에서 유저 제거
  @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channelData.values()) {
      channel.getChannelUsers().removeIf(user -> user.getId().equals(userId));
    }
    saveChannelData();
  }

  /*
 Repository: 데이터를 영속화(storage)하는 계층
데이터를 단순히 저장/조회/삭제/갱신
getChannelById, save, delete, update 같은 CRUD 담당
로직 판단, 유효성 검사, 상태 변경 등 비즈니스 로직은 수행하지 않음

 Service: 비즈니스 로직을 수행하는 계층
도메인 간의 관계 처리, 검증, 상태 변화 등 핵심 로직을 수행
-채널 이름 중복 검사
-채널에 참여자 추가/삭제
-특정 조건 충족 시 예외 던지기
-도메인 간 연계 (예: 유저가 삭제되면 채널에서 제거)
   */
}
