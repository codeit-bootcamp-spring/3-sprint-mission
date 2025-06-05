package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @Query("""
          SELECT c FROM Channel c
          WHERE c.type = 'PUBLIC'
             OR c.id IN (
               SELECT rs.channel.id FROM ReadStatus rs
               WHERE rs.user.id = :userId
             )
      """)
  List<Channel> findAllByUserId(@Param("userId") UUID userId);

  void deleteById(@NonNull UUID channelId);
}
