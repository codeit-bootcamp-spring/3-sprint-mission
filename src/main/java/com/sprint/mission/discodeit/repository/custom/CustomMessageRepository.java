package com.sprint.mission.discodeit.repository.custom;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMessageRepository {

    Slice<Message> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt, Pageable pageable);

    Optional<Instant> findLastMessageAtByChannelId(UUID channelId);
}
