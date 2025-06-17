package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.custom.ReadStatusRepositoryCustom;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID>,
    ReadStatusRepositoryCustom {


    List<ReadStatus> findAllByUserId(UUID userId);

    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
