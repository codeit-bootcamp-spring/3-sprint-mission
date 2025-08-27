package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpqMessageRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface MessageRepository extends JpaRepository<Message, UUID>, MessageRepositoryCustom {
    List<Message> findAllByChannelId(UUID channelId);

    boolean existsById(UUID messageId);

    Message findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

    Long countByChannelId(UUID channelId);
}
