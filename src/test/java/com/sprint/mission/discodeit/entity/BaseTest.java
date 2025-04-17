package com.sprint.mission.discodeit.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BaseTest {

  @Test
  @DisplayName("Base 객체가 생성될 때 고유한 ID가 할당되는지 테스트")
  void createBase_shouldHaveUniqueId() throws InterruptedException {
    // given
    // Set은 중복 값을 허용하지 않으므로 정상적으로 삽입되는지 테스트
    Set<UUID> generatedIds = new HashSet<>();

    // when
    Base base1 = new Base();
    Thread.sleep(100);
    Base base2 = new Base();
    Thread.sleep(100);
    Base base3 = new Base();

    // then
    assertTrue(generatedIds.add(base1.getId()));
    assertTrue(generatedIds.add(base2.getId()));
    assertTrue(generatedIds.add(base3.getId()));
    System.out.println("generatedIds: " + generatedIds);
  }

  @Test
  @DisplayName("Base 객체가 생성될 때 createdAt과 updatedAt이 동일한 값으로 초기화되는지 테스트")
  void createBase_shouldHaveSameCreatedAtAndUpdatedAt() {
    // given
    Base base = new Base();

    // when
    long createdAt = base.getCreatedAt();
    long updatedAt = base.getUpdatedAt();

    // then
    assertEquals(createdAt, updatedAt);
  }

  @Test
  void getId() {
  }

  @Test
  void getCreatedAt() {
  }

  @Test
  void getUpdatedAt() {
  }

  @Test
  @DisplayName("setUpdatedAt() 호출 시 updatedAt 테스트")
  void setUpdatedAt_shouldUpdateUpdatedAtToCurrentTime() throws InterruptedException {
    // given
    Base base = new Base();
    long initialUpdatedAt = base.getUpdatedAt();

    // when
    Thread.sleep(100);
    base.setUpdatedAt();
    long updatedUpdatedAt = base.getUpdatedAt();

    // then
    assertTrue(updatedUpdatedAt > initialUpdatedAt);
  }
}