package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  ReadStatus save(ReadStatus readStatus);

  Optional<ReadStatus> findById(UUID id);

  @Query("SELECT DISTINCT r FROM ReadStatus r JOIN FETCH r.user WHERE r.user.id =:userId ")
  List<ReadStatus> findAllByUserId(@Param("userId") UUID userId);

  @Query("SELECT DISTINCT r FROM ReadStatus r JOIN FETCH r.channel WHERE r.channel.id = :channelId")
  List<ReadStatus> findAllByChannelId(@Param("channelId") UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);
}
