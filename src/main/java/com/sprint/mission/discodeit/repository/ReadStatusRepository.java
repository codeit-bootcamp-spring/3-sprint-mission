package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUserId(UUID userId);

    void deleteAllByChannelId(UUID channelId);

    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user"})
    List<ReadStatus> findAllByChannelId(UUID channelId);

    List<ReadStatus> findByChannel_IdAndNotificationEnabledTrue(UUID channelId);

    @Query("SELECT rs.channel FROM ReadStatus rs " +
        "WHERE rs.channel.type = 'PRIVATE' " +
        "AND rs.user.id IN (:user1, :user2) " +
        "GROUP BY rs.channel " +
        "HAVING COUNT(rs.user.id) = 2")
    Optional<Channel> findPrivateChannelByParticipants(UUID user1, UUID user2);
}
