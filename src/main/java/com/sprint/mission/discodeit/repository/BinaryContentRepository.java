package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    // 바이너리 파일 저장
    BinaryContent saveBinaryContent(BinaryContent binaryContent);

    // 파일 아이디로 찾기
    Optional<BinaryContent> findById(UUID id);

    // 다건 조회
    List<BinaryContent> findAllByIds(List<UUID> ids);

    // 파일 삭제
    boolean deleteById(UUID id);
}
