package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
public interface ChannelService {

    void createChannel(Channel channel);                 // 생성

    Channel readChannel(UUID id);                        // 특정 채널 조회

    List<Channel> readChannelByName(String name);

    List<Channel> readChannelByType(String type);              // 특정 이름의 채널 조회

    List<Channel> readAllChannels();                     // 모든 채널 조회

    Channel updateChannel(UUID id, Channel channel);     // 특정 채널 정보 수정

    boolean deleteChannel(UUID id);                      // 특정 채널 제거
}
