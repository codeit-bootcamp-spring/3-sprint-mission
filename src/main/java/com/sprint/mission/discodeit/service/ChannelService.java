package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 채널 등록
    void create(Channel channel);

    // 단일 채널 조회
    Channel getById(UUID id);

    // 전체 채널 조회
    List<Channel> getAll();

    // 채널 정보 수정
    void update(Channel channel);

    // 채널 삭제
    void delete(UUID id);

}
