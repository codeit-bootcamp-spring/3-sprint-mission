package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BinaryContentTest {

  @Nested
  @DisplayName("BinaryContent 생성")
  class Create {

    @Test
    @DisplayName("BinaryContent 생성 시 기본 정보가 올바르게 생성되어야 한다")
    void shouldCreateBinaryContentWhenCreateUserWithProfileImage() {
      // given
      byte[] data = new byte[]{1, 2, 3};
      String fileName = "example.png";
      String mimeType = "image/png";

      // when
      BinaryContent content1 = BinaryContent.create(data, fileName, mimeType);
      BinaryContent content2 = BinaryContent.create(data, fileName, mimeType);

      // then
      assertAll(
          () -> assertNotNull(content1.getId()),
          () -> assertNotNull(content1.getCreatedAt()),
          () -> assertArrayEquals(data, content1.getData()),
          () -> assertEquals(fileName, content1.getFileName()),
          () -> assertEquals(mimeType, content1.getMimeType()),
          () -> assertThat(content1).as("BinaryContent는 고유한 아이디를 가진다").isNotEqualTo(content2)
      );
    }
  }
}
