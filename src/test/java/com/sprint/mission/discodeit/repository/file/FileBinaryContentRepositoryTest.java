package com.sprint.mission.discodeit.repository.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileBinaryContentRepositoryTest {

  @TempDir
  static Path tempDir;

  private BinaryContentRepository binaryContentRepository;

  @BeforeEach
  void setUp() {
    binaryContentRepository = FileBinaryContentRepository.create(
        new FileStorageImpl(tempDir.toString()));
  }

  @Nested
  class Create {

    @Test
    void 바이너리_컨텐트를_저장해야_한다() {
      BinaryContent contentToSave = BinaryContentFixture.createValid();

      BinaryContent savedContent = binaryContentRepository.save(contentToSave);
      Optional<BinaryContent> loadedContent = binaryContentRepository.findById(
          savedContent.getId());

      assertThat(loadedContent).isPresent();
      assertThat(loadedContent.get().getId()).isEqualTo(savedContent.getId());
      assertThat(loadedContent.get().getFileName()).isEqualTo(contentToSave.getFileName());
      assertThat(loadedContent.get().getContentType()).isEqualTo(contentToSave.getContentType());
    }
  }

  @Nested
  class Read {

    @Test
    void ID로_바이너리_컨텐트를_찾을_수_있어야_한다() {
      BinaryContent savedContent = binaryContentRepository.save(BinaryContentFixture.createValid());

      Optional<BinaryContent> foundContent = binaryContentRepository.findById(savedContent.getId());

      assertThat(foundContent).isPresent();
      assertThat(foundContent.get().getId()).isEqualTo(savedContent.getId());
      assertThat(foundContent.get().getFileName()).isEqualTo(savedContent.getFileName());
      assertThat(foundContent.get().getContentType()).isEqualTo(savedContent.getContentType());
    }

    @Test
    void 존재하지_않는_ID로_바이너리_컨텐트를_찾으면_비어_있는_Optional을_반환해야_한다() {
      UUID nonExistingId = UUID.randomUUID();

      Optional<BinaryContent> foundContent = binaryContentRepository.findById(nonExistingId);

      assertThat(foundContent).isEmpty();
    }
  }

  @Nested
  class Delete {

    @Test
    void ID로_바이너리_컨텐트를_삭제해야_한다() {
      BinaryContent savedContent = binaryContentRepository.save(BinaryContentFixture.createValid());

      binaryContentRepository.delete(savedContent.getId());

      Optional<BinaryContent> deletedContent = binaryContentRepository.findById(
          savedContent.getId());
      assertThat(deletedContent).isEmpty();
    }
  }
}
