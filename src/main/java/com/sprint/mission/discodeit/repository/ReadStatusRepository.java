package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannelId(UUID channelId);

    @Query("SELECT r FROM ReadStatus r " +
            "JOIN FETCH r.user u " +
            "LEFT JOIN FETCH u.profile " +
            "WHERE r.channel.id = :channelId")
    List<ReadStatus> findAllByChannelIdWithUser(UUID channelId);

    void deleteAllByChannelId(UUID channelId);
    
    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
