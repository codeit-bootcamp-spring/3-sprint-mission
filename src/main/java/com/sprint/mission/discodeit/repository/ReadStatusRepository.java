package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  @EntityGraph(attributePaths = {"user", "channel"})
  List<ReadStatus> findAllByUserId(UUID userId);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT rs.channel.id FROM ReadStatus rs WHERE rs.user.id = :userId")
  List<UUID> findChannelIdsByUserId(@Param("userId") UUID userId);

  @Query("SELECT rs.user.id FROM ReadStatus rs WHERE rs.channel.id = :channelId")
  List<UUID> findUserIdsByChannelId(@Param("channelId") UUID channelId);

  @Query("SELECT rs.user FROM ReadStatus rs WHERE rs.channel.id = :channelId")
  List<User> findUsersByChannelId(@Param("channelId") UUID channelId);

}
