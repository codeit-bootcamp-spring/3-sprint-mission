package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN ReadStatus rs ON c.id = rs.channel.id " +
      "WHERE c.type = 'PUBLIC' OR rs.user.id = :userId")
  List<Channel> findAllByUserIdOrPublic(@Param("userId") UUID userId);

}
