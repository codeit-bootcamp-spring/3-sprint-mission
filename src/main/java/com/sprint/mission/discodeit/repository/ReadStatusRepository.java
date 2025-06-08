package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  @Query("SELECT r FROM ReadStatus r JOIN FETCH r.user")
  List<ReadStatus> findAllByUserId(UUID userId);

  @Query("SELECT r FROM ReadStatus r JOIN FETCH r.channel")
  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}