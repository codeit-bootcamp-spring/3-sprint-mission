package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
  // 1. 채널 생성
  Channel save(Channel channel);

  // 2. 채널 단건 조회
  Optional<Channel> getChannelById(UUID channelId);

  // 3. 채널 전체 조회
  List<Channel> getAllChannels();

  // 4. 채널 이름 수정
  void update(Channel channel);

  // 5. 채널 삭제
  void delete(UUID channelId);

  // 6. 유저가 만든 채널 삭제
  void deleteByOwnerId(UUID userId);

  // 7. 유저가 참여 중인 모든 채널에서 삭제
  void removeUserFromAllChannels(UUID userId);
}
