package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

  /**
   * 컨텐트 삽입
   *
   * @param binaryContent BinaryContent
   */
  void insert(BinaryContent binaryContent);

  /**
   * 컨텐트 고유 아이디로 조회
   *
   * @param id UUID
   * @return Optional<BinaryContent>
   */
  Optional<BinaryContent> findById(UUID id);

  /**
   * 컨텐트 저장 또는 업데이트
   *
   * @param binaryContent BinaryContent
   * @return BinaryContent
   */
  BinaryContent save(BinaryContent binaryContent);

  /**
   * 컨텐트 업데이트 (존재하지 않으면 예외)
   *
   * @param binaryContent BinaryContent
   */
  void update(BinaryContent binaryContent);

  /**
   * 컨텐트 고유 id로 삭제
   *
   * @param binaryContentId UUID
   */
  void delete(UUID binaryContentId);
}
