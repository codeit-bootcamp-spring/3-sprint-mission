package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> findAllByReceiverId(UUID receiverId);

  void deleteByIdAndReceiverId(UUID notificationId, UUID receiverId);

  void create(Set<UUID> receiverId, String title, String content);
}
