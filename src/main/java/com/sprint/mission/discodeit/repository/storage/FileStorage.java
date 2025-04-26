package com.sprint.mission.discodeit.repository.storage;

import java.util.List;

public interface FileStorage {

  /**
   * 객체를 파일에 저장하고 저장된 위치(position)를 반환한다.
   *
   * @param obj 저장할 객체 (직렬화 가능해야 함).
   * @return 객체가 저장된 파일상의 위치를 나타내는 {@code long}.
   */
  long saveObject(Object obj);

  /**
   * 파일에서 특정 위치(position)에 저장된 객체를 읽어온다.
   *
   * @param position 파일 내 객체가 저장된 위치.
   * @return 지정된 위치에서 읽어온 객체. 위치가 유효하지 않으면 예외가 발생한다.
   */
  Object readObject(Long position);

  /**
   * 파일의 특정 위치(position)에 저장된 객체를 새로운 객체로 수정한다.
   *
   * @param position 수정할 객체의 파일 내 위치.
   * @param obj      새로 저장할 객체 (직렬화 가능해야 함).
   */
  void updateObject(Long position, Object obj);

  /**
   * 파일의 특정 위치(position)에 저장된 객체를 삭제한다.
   *
   * @param position 삭제할 객체의 파일 내 위치.
   */
  void deleteObject(Long position);

  /**
   * 파일에 저장된 모든 객체를 읽어온다.
   *
   * @return 파일에 저장된 모든 객체의 리스트.
   */
  List<Object> readAll();

  /**
   * 파일 저장소의 단편화를 방지하기 위해 최적화를 수행한다. 이는 파일 내 객체를 재배치하거나 비어 있는 공간을 정리하는 등의 작업을 포함할 수 있다.
   */
  void optimize();
}