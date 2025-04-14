package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService { // 채널에 대한 서비스 인터페이스
    Channel create(String name); // 채널 생성
    Channel findById(UUID id); // ID로 채널 조회
    List<Channel> findAll(); // 모든 채널 조회
    Channel update(UUID id, String newName); // 채널 이름 수정
    void delete(UUID id); // 채널 삭제
}
