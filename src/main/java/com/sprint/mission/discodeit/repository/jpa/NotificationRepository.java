package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : NotificationRepository
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

    void deleteByIdAndReceiverId(UUID id, UUID receiverId);
}
