package com.sprint.mission.discodeit.repository.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.storage.FileStorageImpl;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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
      // given
      UUID contentId = UUID.randomUUID();
      BinaryContent contentToSave = BinaryContentFixture.createValidMessageAttachment();

      // when
      BinaryContent savedContent = binaryContentRepository.save(contentToSave);
      Optional<BinaryContent> loadedContent = binaryContentRepository.findById(
          savedContent.getId());

      // then
      assertThat(loadedContent).isPresent();
      assertThat(loadedContent.get().getId()).isEqualTo(savedContent.getId());
      assertThat(loadedContent.get().getFileName()).isEqualTo(contentToSave.getFileName());
      assertThat(loadedContent.get().getMimeType()).isEqualTo(contentToSave.getMimeType());
      assertThat(loadedContent.get().getContentType()).isEqualTo(contentToSave.getContentType());
      assertThat(loadedContent.get().getMessageId()).isEqualTo(contentToSave.getMessageId());
    }
  }

  @Nested
  class Read {

    @Test
    void ID로_바이너리_컨텐트를_찾을_수_있어야_한다() throws IOException {
      // given
      UUID contentId = UUID.randomUUID();
      BinaryContent savedContent = binaryContentRepository.save(
          BinaryContentFixture.createValidMessageAttachment());

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(savedContent.getId());

      // then
      assertThat(foundContent).isPresent();
      assertThat(foundContent.get().getId()).isEqualTo(savedContent.getId());
      assertThat(foundContent.get().getFileName()).isEqualTo(savedContent.getFileName());
      assertThat(foundContent.get().getMimeType()).isEqualTo(savedContent.getMimeType());
      assertThat(foundContent.get().getContentType()).isEqualTo(savedContent.getContentType());
      assertThat(foundContent.get().getMessageId()).isEqualTo(savedContent.getMessageId());
    }

    @Test
    void 존재하지_않는_ID로_바이너리_컨텐트를_찾으면_비어_있는_Optional을_반환해야_한다() {
      // given
      UUID nonExistingId = UUID.randomUUID();

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findById(nonExistingId);

      // then
      assertThat(foundContent).isEmpty();
    }

    @Test
    void 사용자_ID로_바이너리_컨텐트를_찾을_수_있어야_한다() throws IOException {
      // given
      UUID userId = UUID.randomUUID();
      BinaryContent savedContent = binaryContentRepository.save(
          BinaryContentFixture.createValidProfileImage(userId));

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findByUserId(userId);

      // then
      assertThat(foundContent).isPresent();
      assertThat(foundContent.get().getUserId()).isEqualTo(userId);
      assertThat(foundContent.get().getFileName()).isEqualTo(savedContent.getFileName());
      assertThat(foundContent.get().getMimeType()).isEqualTo(savedContent.getMimeType());
      assertThat(foundContent.get().getContentType()).isEqualTo(savedContent.getContentType());
    }

    @Test
    void 존재하지_않는_사용자_ID로_바이너리_컨텐트를_찾으면_비어_있는_Optional을_반환해야_한다() {
      // given
      UUID nonExistingUserId = UUID.randomUUID();

      // when
      Optional<BinaryContent> foundContent = binaryContentRepository.findByUserId(
          nonExistingUserId);

      // then
      assertThat(foundContent).isEmpty();
    }

    @Test
    void 메시지_ID로_바이너리_컨텐트를_찾을_수_있어야_한다() {
      // given
      BinaryContent savedContent = binaryContentRepository.save(
          BinaryContentFixture.createValidMessageAttachment()
      );

      // 첨부파일을 메시지에 연결
      UUID messageId = UUID.randomUUID();
      savedContent.attachToMessage(messageId);
      binaryContentRepository.save(savedContent);

      // when
      List<BinaryContent> foundContent = binaryContentRepository.findAllByMessageId(messageId);

      // then
      assertThat(foundContent).isNotEmpty();
      BinaryContent actualContent = foundContent.get(0);
      assertThat(actualContent.getMessageId()).isEqualTo(messageId);
      assertThat(actualContent.getFileName()).isEqualTo(savedContent.getFileName());
      assertThat(actualContent.getMimeType()).isEqualTo(savedContent.getMimeType());
      assertThat(actualContent.getContentType()).isEqualTo(savedContent.getContentType());
    }

    @Test
    void 존재하지_않는_메시지_ID로_바이너리_컨텐트를_찾으면_빈_리스트를_반환해야_한다() {
      // given
      UUID nonExistingMessageId = UUID.randomUUID();

      // when
      List<BinaryContent> foundContent = binaryContentRepository.findAllByMessageId(
          nonExistingMessageId);

      // then
      assertThat(foundContent).isEmpty();
    }
  }

  @Nested
  class Delete {

    @Test
    void ID로_바이너리_컨텐트를_삭제해야_한다() {
      // given
      UUID contentId = UUID.randomUUID();
      BinaryContent savedContent = binaryContentRepository.save(
          BinaryContentFixture.createValidMessageAttachment());

      // when
      binaryContentRepository.delete(savedContent.getId());

      // then
      Optional<BinaryContent> deletedContent = binaryContentRepository.findById(
          savedContent.getId());
      assertThat(deletedContent).isEmpty();
    }
  }
}