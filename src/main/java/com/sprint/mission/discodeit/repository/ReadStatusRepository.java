package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.custom.ReadStatusRepositoryCustom;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID>,
    ReadStatusRepositoryCustom {


    List<ReadStatus> findAllByUserId(UUID userId);

    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteAllByChannelId(UUID channelId);

    @Query("""
            select rs.user.id
            from ReadStatus rs
            where rs.channel.id = :channelId
              and rs.notificationEnabled = true
        """)
    List<UUID> findUserIdsByChannelIdAndNotificationEnabledTrue(@Param("channelId") UUID channelId);
}
