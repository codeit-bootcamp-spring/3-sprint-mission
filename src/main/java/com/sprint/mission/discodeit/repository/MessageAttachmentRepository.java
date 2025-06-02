package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {

  List<MessageAttachment> findAllByMessageId(UUID messageId);

  List<MessageAttachment> findAllByAttachmentId(UUID attachmentId);

  void deleteAllByMessageId(UUID messageId);

  void deleteAllByAttachmentId(UUID attachmentId);
}