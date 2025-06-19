package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"channel"})
  public Page<Message> findAllByChannelId(UUID channelId, Pageable pageable
  );

  public void deleteAllByChannelId(UUID channelId);

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
