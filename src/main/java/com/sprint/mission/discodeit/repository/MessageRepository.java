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

  // N+1 문제 해결: 작성자와 첨부파일 정보를 모두 Fetch Join으로 한 번에 조회
  @Query("SELECT DISTINCT m FROM Message m " +
      "LEFT JOIN FETCH m.author " +
      "LEFT JOIN FETCH m.messageAttachments ma " +
      "LEFT JOIN FETCH ma.attachment " +
      "WHERE m.channel.id = :channelId ORDER BY m.createdAt ASC")
  List<Message> findAllByChannelIdWithAuthorAndAttachmentsOrderByCreatedAtAsc(@Param("channelId") UUID channelId);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  // N+1 문제 해결: 페이징 조회 시에도 작성자 정보를 Fetch Join으로 조회
  @Query("SELECT m FROM Message m LEFT JOIN FETCH m.author WHERE m.channel.id = :channelId")
  Page<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId, Pageable pageable);

  // N+1 문제 해결: 페이징 조회 시 작성자와 첨부파일 정보를 모두 Fetch Join으로 조회
  @Query(value = "SELECT DISTINCT m FROM Message m " +
      "LEFT JOIN FETCH m.author " +
      "LEFT JOIN FETCH m.messageAttachments ma " +
      "LEFT JOIN FETCH ma.attachment " +
      "WHERE m.channel.id = :channelId", countQuery = "SELECT COUNT(m) FROM Message m WHERE m.channel.id = :channelId")
  Page<Message> findAllByChannelIdWithAuthorAndAttachments(@Param("channelId") UUID channelId, Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}
