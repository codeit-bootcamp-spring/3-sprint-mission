package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("jcfReadStatusRepository")
public class JCFReadStatusRepository implements ReadStatusRepository {
    @Override
    public void save(ReadStatus readStatus) {
    }

    @Override
    public void deleteByUserId(UUID userId) {
    }

}
