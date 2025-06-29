package com.sprint.mission.discodeit.repository.custom;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface CustomReadStatusRepository {

    List<ReadStatus> findAllByChannelIdWithUser(UUID channelId);

}
