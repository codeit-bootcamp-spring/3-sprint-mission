package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    public void deleteAllByChannelId(UUID channelId);

    public List<Message> findAllByChannelId(UUID channelId);

    public Slice<Message> findAllByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId,
                                                                                     Instant cursor, Pageable pageable);

    public Slice<Message> findAllByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    public Optional<Message> findTop1ByChannelIdOrderByCreatedAtDesc(UUID channelId);
}
