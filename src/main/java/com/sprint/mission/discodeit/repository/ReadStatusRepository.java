package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    void deleteAllByChannelId(UUID channelId);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    List<ReadStatus> findAllByUserId(UUID userId);
}
