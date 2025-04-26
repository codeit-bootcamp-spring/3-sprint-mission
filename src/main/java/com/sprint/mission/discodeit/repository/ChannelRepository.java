package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelRepository {

    Channel save(Channel channel); // 채널 저장/업데이트

    Channel findById(UUID channelId); // 채널 조회

    List<Channel> findAll(); // 전체 채널 목록

    void deleteById(UUID channelId); // 채널 삭제
}
