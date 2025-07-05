package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BinaryContentTest {

  @Nested
  class Create {

    @Test
    void BinaryContent_생성_시_기본_정보가_올바르게_생성되어야_한다() {
      // given
      BinaryContent content = BinaryContentFixture.createValid();

      // when & then
      assertAll(
          () -> assertNotNull(content.getId(), "ID는 null이 아니어야 한다"),
          () -> assertNotNull(content.getCreatedAt(), "생성 시간은 null이 아니어야 한다"),
          () -> assertArrayEquals(BinaryContentFixture.getDefaultData(), content.getBytes(),
              "데이터가 올바르게 설정되어야 한다"),
          () -> assertEquals(BinaryContentFixture.getDefaultFileName(), content.getFileName(),
              "파일명이 올바르게 설정되어야 한다"),
          () -> assertEquals(BinaryContentFixture.getDefaultMimeType(), content.getContentType(),
              "MIME 타입이 올바르게 설정되어야 한다"),
          () -> assertEquals(BinaryContentFixture.getDefaultData().length, content.getSize(),
              "파일 크기가 올바르게 설정되어야 한다")
      );
    }

    @Test
    void BinaryContent_생성_시_고유한_ID를_가져야_한다() {
      // given
      BinaryContent content1 = BinaryContentFixture.createValid();
      BinaryContent content2 = BinaryContentFixture.createValid();

      // when & then
      assertThat(content1).as("BinaryContent 객체는 서로 달라야 한다").isNotEqualTo(content2);
      assertThat(content1.getId()).as("BinaryContent ID는 고유해야 한다").isNotEqualTo(content2.getId());
    }

    @Test
    void 빌더로_BinaryContent_생성_시_기본_정보가_올바르게_설정되어야_한다() {
      UUID id = UUID.randomUUID();
      Instant now = Instant.now();
      String fileName = "hello.jpg";
      String contentType = "image/jpeg";
      byte[] bytes = "image".getBytes();

      BinaryContent content = BinaryContent.builder()
          .id(id)
          .createdAt(now)
          .fileName(fileName)
          .size(123L)
          .contentType(contentType)
          .bytes(bytes)
          .build();

      assertThat(content.getId()).isEqualTo(id);
      assertThat(content.getCreatedAt()).isEqualTo(now);
      assertThat(content.getFileName()).isEqualTo(fileName);
      assertThat(content.getSize()).isEqualTo(123L);
      assertThat(content.getContentType()).isEqualTo(contentType);
      assertThat(content.getBytes()).isEqualTo(bytes);
    }

    @Test
    void createWithNow_현재_시간으로_생성된다() {
      // given
      Instant before = Instant.now();
      BinaryContent content = BinaryContent.createWithNow(
          "test.txt",
          1234L,
          "text/plain",
          new byte[]{1, 2, 3}
      );
      Instant after = Instant.now();

      // then
      assertThat(content.getId()).isNotNull();
      assertThat(content.getCreatedAt()).isBetween(before, after);
      assertThat(content.getFileName()).isEqualTo("test.txt");
      assertThat(content.getSize()).isEqualTo(1234L);
      assertThat(content.getContentType()).isEqualTo("text/plain");
      assertThat(content.getBytes()).containsExactly(1, 2, 3);
    }

    @Test
    void createWithTimestamp_지정된_시간으로_생성된다() {
      // given
      Instant fixed = Instant.parse("2024-01-01T00:00:00Z");
      BinaryContent content = BinaryContent.createWithTimestamp(
          fixed,
          "test.jpg",
          2048L,
          "image/jpeg",
          new byte[]{10, 20}
      );

      // then
      assertThat(content.getCreatedAt()).isEqualTo(fixed);
      assertThat(content.getFileName()).isEqualTo("test.jpg");
      assertThat(content.getSize()).isEqualTo(2048L);
      assertThat(content.getContentType()).isEqualTo("image/jpeg");
      assertThat(content.getBytes()).containsExactly(10, 20);
    }

    @Test
    void createWithTimestamp_null이면_예외발생() {
      assertThatThrownBy(() -> BinaryContent.createWithTimestamp(
          null, "a", 1L, "t", new byte[1]
      )).isInstanceOf(NullPointerException.class);
    }
  }
}
