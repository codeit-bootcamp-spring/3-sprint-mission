package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository
 * FileName     : MessageRepositoryCustom
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */
public interface MessageRepositoryCustom {
    List<Message> findSliceByCursor(
        UUID channelId,
        @Nullable Instant cursorCreatedAt,   // null ⇒ 첫 페이지
        Pageable pageable
    );
}
