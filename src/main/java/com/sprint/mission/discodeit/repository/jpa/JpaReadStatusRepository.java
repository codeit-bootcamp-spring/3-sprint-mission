package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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

    boolean existsByUser(User user);

    boolean existsByUserAndChannel(User user, Channel channel);

    List<ReadStatus> findAllByUserId(UUID userId);

    List<ReadStatus> findAllByChannel(Channel channel);
}
