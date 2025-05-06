package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BinaryContentTest {

  @Nested
  @DisplayName("BinaryContent 생성")
  class Create {

    @Test
    @DisplayName("BinaryContent 생성 시 기본 정보가 올바르게 생성되어야 한다")
    void shouldCreateBinaryContentWithCorrectInfo() {
      // 테스트용 프로필 이미지를 생성한다
      UUID userId = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValidProfileImage(userId);

      // BinaryContent 기본 정보 검증
      assertAll(
          () -> assertNotNull(content.getId(), "ID는 null이 아니어야 한다"),
          () -> assertNotNull(content.getCreatedAt(), "생성 시간은 null이 아니어야 한다"),
          () -> assertArrayEquals(BinaryContentFixture.getDefaultData(), content.getBytes(),
              "데이터가 올바르게 설정되어야 한다"),
          () -> assertEquals(BinaryContentFixture.getDefaultFileName(), content.getFileName(),
              "파일명이 올바르게 설정되어야 한다"),
          () -> assertEquals(BinaryContentFixture.getDefaultMimeType(), content.getMimeType(),
              "MIME 타입이 올바르게 설정되어야 한다"),
          () -> assertEquals(userId, content.getUserId(), "사용자 ID가 올바르게 설정되어야 한다")
      );
    }

    @Test
    @DisplayName("BinaryContent 생성 시 고유한 ID를 가져야 한다")
    void shouldHaveUniqueId() {
      // 테스트용 프로필 이미지를 두 개 생성한다
      UUID userId = UUID.randomUUID();
      BinaryContent content1 = BinaryContentFixture.createValidProfileImage(userId);
      BinaryContent content2 = BinaryContentFixture.createValidProfileImage(userId);

      // 서로 다른 객체임을 검증
      assertThat(content1).as("BinaryContent 객체는 서로 달라야 한다").isNotEqualTo(content2);
      assertThat(content1.getId()).as("BinaryContent ID는 고유해야 한다").isNotEqualTo(content2.getId());
    }
  }
}
