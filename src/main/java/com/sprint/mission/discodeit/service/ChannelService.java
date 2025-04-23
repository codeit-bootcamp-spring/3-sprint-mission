package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {

    Channel createChannel(String channelName, String channelDesc, String createrName);      // Create 새로운 채널 생성

    void printAllChannels();                                                                // Read 전체 채널 리스트 읽기
    Channel findChannelByName(String name);                                                 // Read 이름으로 단일채널 읽기

    void UpdateChannel(Channel channel, String channelName, String channelDescription);     // Update 채널 정보 수정

    void deleteChannel(Channel channel);                                                    // Delete 채널 삭제
}
