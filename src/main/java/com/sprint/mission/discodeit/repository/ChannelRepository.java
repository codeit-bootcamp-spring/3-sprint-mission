package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @Query("""
          select c from Channel c
          where c.type = 'PUBLIC'
            or c in (
              select rs.channel from ReadStatus rs
              where rs.user.id = :userId 
            )
        """)
    public List<Channel> findAllAccessibleByUser(UUID userId);

}
