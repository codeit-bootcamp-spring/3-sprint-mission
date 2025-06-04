package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannel_Id(UUID channelId);

    void deleteByChannel_Id(UUID channelId);

    Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

    Slice<Message> findAllByChannel_IdAndCreatedAtBefore(UUID channelId, Instant createdAtBefore,
            Pageable pageable);

    Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);
}
