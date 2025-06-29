package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.custom.CustomReadStatusRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID>,
    CustomReadStatusRepository {

    // 유저 아이디와 채널 아이디로 조회
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 유저 아이디로 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 채널 아이디로 조회
    List<ReadStatus> findAllByChannelId(UUID channelId);

}