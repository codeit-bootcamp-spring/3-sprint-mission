package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@EnableJpaAuditing
public class BinaryContentTest {

  @Autowired
  TestEntityManager em;

  @Nested
  class Create {

    @Test
    void BinaryContent_생성_시_DB에_저장되고_ID와_시간이_설정되어야_한다() {
      BinaryContent content = BinaryContentFixture.createValid();
      em.persist(content);
      em.flush();
      em.clear();

      BinaryContent found = em.find(BinaryContent.class, content.getId());

      assertAll(
          () -> assertNotNull(found.getId()),
          () -> assertNotNull(found.getCreatedAt()),
          () -> assertEquals(BinaryContentFixture.getDefaultFileName(), found.getFileName()),
          () -> assertEquals(BinaryContentFixture.getDefaultMimeType(), found.getContentType()),
          () -> assertEquals(BinaryContentFixture.getDefaultData().length, found.getSize())
      );
    }

    @Test
    void BinaryContent_생성_시_고유한_ID를_가져야_한다() {
      BinaryContent content1 = BinaryContentFixture.createValid();
      BinaryContent content2 = BinaryContentFixture.createValid();

      em.persist(content1);
      em.persist(content2);
      em.flush();

      assertThat(content1.getId()).isNotEqualTo(content2.getId());
    }

    @Test
    void 빌더로_생성한_BinaryContent는_정확한_값을_가져야_한다() {
      String fileName = "hello.jpg";
      String contentType = "image/jpeg";
      byte[] bytes = "image".getBytes();

      BinaryContent content = BinaryContent.builder()
          .fileName(fileName)
          .size(123L)
          .contentType(contentType)
          .build();

      em.persist(content);
      em.flush();
      em.clear();

      BinaryContent found = em.find(BinaryContent.class, content.getId());

      assertAll(
          () -> assertEquals(fileName, found.getFileName()),
          () -> assertEquals(contentType, found.getContentType()),
          () -> assertEquals(123L, found.getSize())
      );
    }
  }
}
