package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Profile("jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private static final Map<UUID, BinaryContent> binaryContents = new HashMap<>();

    @Override
    public void save(BinaryContent userProfileImage) { binaryContents.put(userProfileImage.getId(), userProfileImage); }

    @Override
    public BinaryContent loadById(UUID id) { return binaryContents.get(id); }

    @Override
    public List<BinaryContent> loadAll() { return binaryContents.values().stream().toList(); }

    @Override
    public void delete(UUID id) { binaryContents.remove(id); }
}
