package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  List<ReadStatus> findAllByChannelId(UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  void deleteAllByChannelId(UUID channelId);

  boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

  List<ReadStatus> findAllByChannelIdAndUser(UUID channelId, User user);
}
