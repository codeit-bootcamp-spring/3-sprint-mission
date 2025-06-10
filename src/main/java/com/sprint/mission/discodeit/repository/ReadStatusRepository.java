package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUser_Id(UUID userId);

    List<ReadStatus> findAllByChannel_Id(UUID channelId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    void deleteAllByChannel_Id(UUID channelId);
}
