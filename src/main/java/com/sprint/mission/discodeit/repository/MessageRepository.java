package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  public List<Message> findAllByChannelId(UUID channelId);

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
