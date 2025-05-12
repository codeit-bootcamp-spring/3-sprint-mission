package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
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

    //Update: 채널 참가
    // boolean joinChannel(UUID channelId, UUID userId, String password);

    // Update: 특정 채널 정보 업데이트
    Channel updateChannel(PublicChannelUpdateRequest request);

    // Delete: 채널 나가기
    // boolean leaveChannel(UUID channelId, UUID userId);
    // Delete: 특정 채널 삭제
    void deleteChannel(UUID channelId);
}
