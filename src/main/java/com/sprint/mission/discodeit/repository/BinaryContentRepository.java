package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    // 콘텐츠 저장
    BinaryContent save(BinaryContent content);

    // ID로 콘텐츠 조회
    BinaryContent findById(UUID id);

    // 전체 콘텐츠 조회
    List<BinaryContent> findAll();

    // ID로 콘텐츠 삭제
    void deleteById(UUID id);
}