package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository extends AbstractFileRepository<Channel, UUID> implements
    ChannelRepository {

  public FileChannelRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory
  ) {
    super(Paths.get(System.getProperty("user.dir"), fileDirectory, "channel.ser").toString());
  }

  @Override
  public Channel save(Channel channel) {
    super.save(channel, channel.getId());
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    return super.findById(channelId);
  }

  @Override
  public List<Channel> findAll() {
    return super.findAll();
  }

  @Override
  public void update(Channel channel) {
    super.update(channel, channel.getId());
  }

  @Override
  public void delete(UUID channelId) {
    super.delete(channelId);
  }

  @Override
  public void deleteByOwnerId(UUID userId) {
    lock.lock();
    try {
      dataMap.entrySet()
          .removeIf(entry -> entry.getValue().getChannelOwner().getId().equals(userId));
      saveData();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    lock.lock();
    try {
      for (Channel channel : dataMap.values()) {
        channel.getChannelMembers().removeIf(user -> user.getId().equals(userId));
      }
      saveData();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean existsById(UUID channelId) {
    return super.existsById(channelId);
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
