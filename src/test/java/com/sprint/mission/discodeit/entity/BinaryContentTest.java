package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BinaryContentTest {

  @Nested
  class Create {

    @Test
    void BinaryContent_생성_시_기본_정보가_올바르게_생성되어야_한다() {
      // given
      UUID userId = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValidProfileImage(userId);

      // when & then
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
    void BinaryContent_생성_시_고유한_ID를_가져야_한다() {
      // given
      UUID userId = UUID.randomUUID();
      BinaryContent content1 = BinaryContentFixture.createValidProfileImage(userId);
      BinaryContent content2 = BinaryContentFixture.createValidProfileImage(userId);

      // when & then
      assertThat(content1).as("BinaryContent 객체는 서로 달라야 한다").isNotEqualTo(content2);
      assertThat(content1.getId()).as("BinaryContent ID는 고유해야 한다").isNotEqualTo(content2.getId());
    }
  }
}
