package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelRepository {

    Channel save(Channel channel); // 채널 저장/업데이트

    Optional<Channel> findById(UUID channelId); // 채널 조회

    List<Channel> findAll(); // 전체 채널 목록

    boolean existsById(UUID channelId); // 채널 존재 여부

    void deleteById(UUID channelId); // 채널 삭제

    List<Channel> findAllById(Iterable<UUID> ids); // ID 목록으로 채널 조회 추가
}
