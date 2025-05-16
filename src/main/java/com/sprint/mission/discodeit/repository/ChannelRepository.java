package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {

    // 채널 생성
    Channel saveChannel(Channel channel);

    //전체 채널 조회
    List<Channel> findAllChannels();

    // 채널 이름으로 조회
    Optional<Channel> findChannelUsingName(String channelName);

    // 채널 아이디로 조회
    Optional<Channel> findChannelUsingId(UUID channelId);

    //채널 삭제
    void deleteChannel(UUID channelId);
}
