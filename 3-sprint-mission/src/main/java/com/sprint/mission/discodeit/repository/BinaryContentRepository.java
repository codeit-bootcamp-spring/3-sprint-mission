package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    BinaryContent save(BinaryContent binaryContent);
    List<BinaryContent> findAll();
    BinaryContent find(UUID id);
    void delete(UUID id) throws IOException;
    void deleteByUserId(UUID userId) throws IOException;
}
