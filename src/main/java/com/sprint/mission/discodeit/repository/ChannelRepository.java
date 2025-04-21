package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

// Channel 저장소를 위한 공통 인터페이스
public interface ChannelRepository {
    void save(Channel channel);            // 채널 저장
    Channel findById(UUID id);             // ID로 채널 조회
    List<Channel> findAll();               // 전체 채널 목록 조회
    void update(Channel channel);          // 채널 정보 수정
    void delete(UUID id);                  // 채널 삭제
}