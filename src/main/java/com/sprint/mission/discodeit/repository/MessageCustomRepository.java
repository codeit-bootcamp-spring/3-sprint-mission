package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public interface MessageCustomRepository {
    List<Message> findByChannelIdWithCursor(UUID channelId, Instant cursor, Sort.Direction direction, int limit);

}
