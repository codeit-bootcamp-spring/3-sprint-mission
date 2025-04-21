package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(String name);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String newName); // 수정된 리턴 타입
    Channel delete(UUID id);                 // 수정된 리턴 타입
}
