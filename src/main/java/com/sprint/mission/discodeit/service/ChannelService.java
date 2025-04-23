package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

// 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
public interface ChannelService {

    // 생성
    Channel create(ChannelType type, String ChannelName, String category);

    // 특정 채널 조회
    Channel find(UUID id);

    // 모든 채널 조회
    List<Channel> findAll();

    // 특정 채널 정보 수정
    Channel update(UUID id, String newName, String newCategory);

    // 특정 채널 제거
    void delete(UUID id);
}
