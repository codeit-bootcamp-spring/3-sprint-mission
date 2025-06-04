package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "attachments"})
    public List<Message> findAllByChannelId(UUID channelId);
    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "attachments"})
    public Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "attachments"})
    public Slice<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId, Instant cursor, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "attachments"})
    public Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);
    public void deleteAllByChannelId(UUID channelId);
}
