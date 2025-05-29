package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "channel"})
    Page<Message> findPageByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    Slice<Message> findSliceByChannelId(UUID channelId, Pageable pageable);
}
