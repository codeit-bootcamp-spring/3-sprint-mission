package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MessageAttachmentRepository extends JpaRepository<Message, UUID> {

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM MessageAttachment ma WHERE ma.message.id IN :messageIds")
  void deleteByMessageIds(@Param("messageIds") List<UUID> messageIds);
}
