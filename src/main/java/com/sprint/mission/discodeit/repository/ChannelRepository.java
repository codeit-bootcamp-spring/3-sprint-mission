package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

  /**
   * 채널 객체의 고유 아이디로 조회
   *
   * @param id UUID
   * @return Optional<Channel>
   */
  Optional<Channel> findById(UUID id);

  /**
   * 모든 채널 조회
   *
   * @return List<Channel>
   */
  List<Channel> findAll();

  /**
   * 채널 저장
   *
   * @param channel Channel
   * @return Channel
   */
  Channel save(Channel channel);

  /**
   * 채널 객체의 고유 아이디로 삭제
   *
   * @param id UUID
   */
  void delete(UUID id);
}
