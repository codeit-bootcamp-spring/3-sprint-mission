package com.sprint.mission.discodeit.repository.jcf;

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
      BinaryContent contentToSave = BinaryContentFixture.createValid();

      BinaryContent savedContent = binaryContentRepository.save(contentToSave);

      assertNotNull(savedContent.getId());
      assertEquals(contentToSave.getBytes(), savedContent.getBytes());
      assertEquals(contentToSave.getFileName(), savedContent.getFileName());
      assertEquals(contentToSave.getContentType(), savedContent.getContentType());
    }
  }

  @Nested
  class Read {

    @Test
    void findById로_저장된_BinaryContent를_조회한다() {
      BinaryContent contentToSave = BinaryContentFixture.createValid();
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);

      Optional<BinaryContent> foundContent = binaryContentRepository.findById(savedContent.getId());

      assertTrue(foundContent.isPresent());
      assertEquals(savedContent, foundContent.get());
    }

    @Test
    void findById로_존재하지_않는_BinaryContent를_조회하면_비어_있는_Optional을_반환한다() {
      UUID id = UUID.randomUUID();

      Optional<BinaryContent> foundContent = binaryContentRepository.findById(id);

      assertTrue(foundContent.isEmpty());
    }

    @Test
    void deleteById로_저장된_BinaryContent를_삭제하고_조회하면_찾을_수_없다() {
      BinaryContent contentToSave = BinaryContentFixture.createValid();
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);
      UUID idToDelete = savedContent.getId();

      binaryContentRepository.delete(idToDelete);
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(idToDelete);

      assertTrue(foundContent.isEmpty());
    }
  }
}
