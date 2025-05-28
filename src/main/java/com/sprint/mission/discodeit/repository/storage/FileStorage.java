package com.sprint.mission.discodeit.repository.storage;

import java.util.List;
import java.util.UUID;

/**
 * 객체별 파일 저장소 인터페이스.
 * <p>
 * 각 객체를 개별 파일로 분리해 저장 및 관리하는 기능을 제공한다. 파일명은 객체의 고유 식별자(UUID 등)를 기반으로 결정되며, 각 파일은 독립적으로 존재하여 동시성 문제를
 * 최소화할 수 있다.
 */
public interface FileStorage<T> {

  /**
   * 객체를 파일에 저장한다. 저장 시 객체의 ID (UUID)를 파일명으로 사용한다.
   *
   * @param id  저장할 객체의 고유 식별자 (파일명을 결정하는 ID).
   * @param obj 저장할 객체 (직렬화 가능해야 함).
   */
  void saveObject(UUID id, T obj);

  /**
   * 지정한 ID에 해당하는 파일에서 객체를 읽어온다.
   *
   * @param id 객체를 식별하는 고유 ID (파일명으로 사용됨).
   * @return 파일에서 읽어온 객체.
   */
  T readObject(UUID id);

  /**
   * 지정한 ID에 해당하는 파일의 객체를 새 객체로 수정(덮어쓰기)한다.
   *
   * @param id  수정할 객체의 고유 ID (파일명으로 사용됨).
   * @param obj 새로 저장할 객체 (직렬화 가능해야 함).
   */
  void updateObject(UUID id, Object obj);

  /**
   * 지정한 ID에 해당하는 파일을 삭제한다.
   *
   * @param id 삭제할 객체의 고유 ID (파일명으로 사용됨).
   */
  void deleteObject(UUID id);

  /**
   * 현재 저장소 내의 모든 객체를 읽어온다.
   *
   * @return 모든 객체의 리스트.
   */
  List<T> readAll();
}
