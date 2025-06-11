package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    public List<ReadStatus> findAllByUserId(UUID userId);

    public void deleteAllByChannelId(UUID channelId);

    Optional<Object> findByUserIdAndChannelId(UUID userId, UUID channelId);

    //비공개 채널 참여자 추출
    @EntityGraph(attributePaths = {"user"})
    public List<ReadStatus> findAllByChannelId(UUID channelId);
}
