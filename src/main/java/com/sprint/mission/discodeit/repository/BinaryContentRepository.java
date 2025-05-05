package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
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
   * 유저 id로 프로필 이미지 조회
   *
   * @param userId UUID
   * @return Optional<BinaryContent>
   */
  Optional<BinaryContent> findByUserId(UUID userId);

  /**
   * 메시지 id로 컨텐트 조회
   *
   * @param messageId UUID
   * @return List<BinaryContent>
   */
  List<BinaryContent> findAllByMessageId(UUID messageId);

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
   * @param id UUID
   */
  void delete(UUID id);

  /**
   * 메시지 id로 해당 메시지와 관련된 컨텐트 삭제
   *
   * @param messageId UUID
   */
  void deleteAllByMessageId(UUID messageId);
}
