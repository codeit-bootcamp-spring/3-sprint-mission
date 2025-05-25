package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create: 새 공개 채널 생성
    Channel createChannel(PublicChannelCreateRequest request);

    // Create: 새 비공개 채널 생성
    Channel createChannel(PrivateChannelCreateRequest request);

    // Read: 채널 ID를 통해 단일 채널 조회
    Channel getChannelById(UUID channelId);

    // Read: 모든 채널 조회
    List<Channel> getAllChannels();

    // Read: 특정 사용자가 볼 수 있는 모든 채널 목록 조회
    List<ChannelDto> getChannelsByUserId(UUID userId);

    // Update: 특정 채널 정보 업데이트
    Channel updateChannel(PublicChannelUpdateRequest request);

    // Delete: 특정 채널 삭제
    void deleteChannel(UUID channelId);

}
