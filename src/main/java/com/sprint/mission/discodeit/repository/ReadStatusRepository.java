package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  @EntityGraph(attributePaths = {"user", "channel"})
  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  @EntityGraph(attributePaths = {"user"})
  ReadStatus findByChannelId(UUID channelId);

  @EntityGraph(attributePaths = {"channel"})
  List<ReadStatus> findAllByUserId(UUID userId);

  @EntityGraph(attributePaths = {"user"})
  List<ReadStatus> findAllByChannelId(UUID channelId);

  void deleteById(@NonNull UUID readStatusId);
}
