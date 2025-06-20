package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"channel"})
  public Page<Message> findAllByChannelId(UUID channelId, Pageable pageable
  );

  void deleteAllByChannelId(UUID channelId);

  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author a "
      + "JOIN FETCH a.status "
      + "LEFT JOIN FETCH a.profile "
      + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
  Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable);

//      /* CrudRepository의 기본 메소드 */
//    public Message save(Message message);
//
//      /* CrudRepository의 기본 메소드 */
//    public Optional<Message> findById(UUID messageId);
//
//      /* JpaRepository의 기본 메소드 */
//    public List<Message> findAll();
//
//      /* CrudRepository의 기본 메소드 */
//    public boolean existsById(UUID messageId);
//
//      /* CrudRepository의 기본 메소드 */
//    public void deleteById(UUID messageId);
}
