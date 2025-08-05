package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpaChannelRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    List<Channel> findAllByType(ChannelType type);
}
