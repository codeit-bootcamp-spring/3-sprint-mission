package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Message save(Message message);

    Optional<Message> findById(UUID messageId);

    List<Message> findByChannelId(UUID channelId);

    Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    void deleteById(UUID messageId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") UUID channelId);

    @Modifying
    @Query("DELETE FROM Message m WHERE m.author.id = :authorId")
    void deleteByAuthorId(@Param("authorId") UUID authorId);
}
