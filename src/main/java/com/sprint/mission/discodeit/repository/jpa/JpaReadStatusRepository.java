package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaReadStatusRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface JpaReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByChannelId(UUID channelId);

    boolean existsByUserAndChannel(User user, Channel channel);

    List<ReadStatus> findAllByUserId(UUID userId);

    @Query("SELECT m FROM ReadStatus m LEFT JOIN FETCH m.channel WHERE m.user.id = :userId")
    List<ReadStatus> findAllByUserIdWithChannel(@Param("userId") UUID userId);

    @Query("SELECT m from ReadStatus  m LEFT JOIN FETCH m.user where m.channel = :channel")
    List<ReadStatus> findAllByChannelWithUser(@Param("channel") Channel channel );
}
