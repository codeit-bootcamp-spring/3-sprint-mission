package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import jakarta.websocket.Decoder;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    BinaryContent save(BinaryContent binaryContent);
    BinaryContent findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    List<BinaryContent> findAll();
    boolean existsById(UUID id);
    void delete(UUID id);
}
