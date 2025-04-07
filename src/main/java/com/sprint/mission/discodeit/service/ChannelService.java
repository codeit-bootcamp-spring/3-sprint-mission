package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;
import java.util.Set;

public interface ChannelService {

    // Create: 새 채널 생성
    Channel createChannel(String channelName, boolean isPrivate, String password, UUID ownerChannelId);

    // Read: 채널 ID를 통해 단일 채널 조회
    Channel getChannelById(UUID channelId);

    // Read: 모든 채널 조회
    List<Channel> getAllChannels();

    // Read: 채널 참가자 목록 조회
    Set<UUID> getChannelParticipants(UUID channelId);

    //Update: 채널 참가
    boolean joinChannel(UUID channelId, UUID userId, String password);

    // Update: 특정 채널 정보 업데이트
    void updateChannel(UUID channelId, String channelName, boolean isPrivate, String password);

    // Delete: 채널 나가기
    boolean leaveChannel(UUID channelId, UUID userId);
    // Delete: 특정 채널 삭제
    void deleteChannel(UUID channelId);
}