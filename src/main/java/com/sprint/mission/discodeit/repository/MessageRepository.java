package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId);

  // N+1 문제 해결: 작성자 정보를 Fetch Join으로 한 번에 조회
  @Query("SELECT m FROM Message m LEFT JOIN FETCH m.author WHERE m.channel.id = :channelId ORDER BY m.createdAt ASC")
  List<Message> findAllByChannelIdWithAuthorOrderByCreatedAtAsc(@Param("channelId") UUID channelId);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  // N+1 문제 해결: 페이징 조회 시에도 작성자 정보를 Fetch Join으로 조회
  @Query("SELECT m FROM Message m LEFT JOIN FETCH m.author WHERE m.channel.id = :channelId")
  Page<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId, Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}
