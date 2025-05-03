package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("jcfBinaryContentRepository")
public class JCFBinaryContentRepository implements BinaryContentRepository {

    @Override
    public void save(BinaryContent userProfileImage) {

    }

    @Override
    public BinaryContent loadByUserId(UUID userId) {
        return null;
    }

    @Override
    public BinaryContent loadById(UUID userId) {
        return null;
    }

    @Override
    public List<BinaryContent> loadAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id, UUID userId) {

    }
}
