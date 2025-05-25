package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // Create: 새 바이너리 콘텐츠 저장
    BinaryContent save(BinaryContentCreateRequest request);

    // Create: 여러 바이너리 콘텐츠 저장
    List<BinaryContent> saveAll(List<BinaryContentCreateRequest> requests);

    // Read: 바이너리 콘텐츠 ID를 통해 조회
    BinaryContent findById(UUID id);

    // Read: 여러 바이너리 콘텐츠 ID를 통해 조회
    List<BinaryContent> findAllByIds(List<UUID> ids);

    // Delete: 바이너리 콘텐츠 ID를 통해 삭제
    void deleteById(UUID id);

    // Delete: 여러 바이너리 콘텐츠 ID를 통해 삭제
    void deleteAllByIds(List<UUID> ids);
}
