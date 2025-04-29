package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BinaryContentTest {

  private byte[] testData;
  private String testFileName;
  private String testMimeType;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    // 테스트 데이터 초기화로 독립성 보장
    testData = new byte[]{1, 2, 3};
    testFileName = "example.png";
    testMimeType = "image/png";
    testUserId = UUID.randomUUID();
  }

  @Nested
  @DisplayName("BinaryContent 생성")
  class Create {

    @Test
    @DisplayName("BinaryContent 생성 시 기본 정보가 올바르게 생성되어야 한다")
    void shouldCreateBinaryContentWithCorrectInfo() {
      // when
      BinaryContent content = BinaryContentFixture.createCustomMessageAttachment(
          testData, testFileName, testMimeType, testUserId);

      // then
      assertAll(
          "BinaryContent 기본 정보 검증",
          () -> assertNotNull(content.getId(), "ID는 null이 아니어야 함"),
          () -> assertNotNull(content.getCreatedAt(), "생성 시간은 null이 아니어야 함"),
          () -> assertArrayEquals(testData, content.getData(), "데이터가 올바르게 설정되어야 함"),
          () -> assertEquals(testFileName, content.getFileName(), "파일명이 올바르게 설정되어야 함"),
          () -> assertEquals(testMimeType, content.getMimeType(), "MIME 타입이 올바르게 설정되어야 함")
      );
    }

    @Test
    @DisplayName("각 BinaryContent는 고유한 ID를 가져야 한다")
    void shouldHaveUniqueId() {
      // when
      BinaryContent content1 = BinaryContentFixture.createCustomMessageAttachment(
          testData, testFileName, testMimeType, testUserId);
      BinaryContent content2 = BinaryContentFixture.createCustomMessageAttachment(
          testData, testFileName, testMimeType, testUserId);

      // then
      assertThat(content1).as("BinaryContent는 고유한 아이디를 가진다").isNotEqualTo(content2);
      assertThat(content1.getId()).as("BinaryContent ID는 고유해야 함").isNotEqualTo(content2.getId());
    }
  }
}