package com.sprint.mission.discodeit.repository.jcf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JCFBinaryContentRepositoryTest {

  private JCFBinaryContentRepository binaryContentRepository;

  @BeforeEach
  void setUp() {
    binaryContentRepository = new JCFBinaryContentRepository();
  }

  @Nested
  class Create {

    @Test
    void save로_BinaryContent를_저장하고_ID가_할당된다() {
      // given
      BinaryContent contentToSave = BinaryContentFixture.createValidProfileImage(UUID.randomUUID());

      // when
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);

      // then
      assertNotNull(savedContent.getId());
      assertEquals(contentToSave.getBytes(), savedContent.getBytes());
      assertEquals(contentToSave.getFileName(), savedContent.getFileName());
      assertEquals(contentToSave.getMimeType(), savedContent.getMimeType());
    }
  }

  @Nested
  class Read {

    @Test
    void findById로_저장된_BinaryContent를_조회한다() {
      // given
      BinaryContent contentToSave = BinaryContentFixture.createValidProfileImage(UUID.randomUUID());
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(savedContent.getId());

      // then
      assertTrue(foundContent.isPresent());
      assertEquals(savedContent, foundContent.get());
    }

    @Test
    void findById로_존재하지_않는_BinaryContent를_조회하면_비어_있는_Optional을_반환한다() {
      // given
      UUID id = UUID.randomUUID();

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(id);

      // then
      assertTrue(foundContent.isEmpty());
    }

    @Test
    void deleteById로_저장된_BinaryContent를_삭제하고_조회하면_찾을_수_없다() {
      // given
      BinaryContent contentToSave = BinaryContentFixture.createValidMessageAttachment();
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);
      UUID idToDelete = savedContent.getId();

      // when
      binaryContentRepository.delete(idToDelete);
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(idToDelete);

      // then
      assertTrue(foundContent.isEmpty());
    }

    @Test
    void findByUserId로_저장된_BinaryContent를_조회한다() {
      // given
      UUID userId = UUID.randomUUID();
      BinaryContent contentToSave = BinaryContentFixture.createValidProfileImage(userId);

      // when
      binaryContentRepository.save(contentToSave);
      BinaryContent foundContent = binaryContentRepository.findByUserId(userId).get();

      // then
      assertThat(foundContent.getUserId()).isEqualTo(userId);
    }
  }
}