package com.sprint.mission.discodeit.repository.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileBinaryContentRepositoryTest {

  @TempDir
  static Path tempDir;

  private BinaryContentRepository binaryContentRepository;
  private Path filePath;

  @BeforeEach
  void setUp() {
    filePath = tempDir.resolve("user-status.ser");
    binaryContentRepository = FileBinaryContentRepository.from(filePath.toString());
  }

  @Test
  @DisplayName("[File] ID로 바이너리 컨텐트를 찾을 수 있어야 한다")
  void findByIdShouldReturnBinaryContentIfExists() throws IOException {
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
  @DisplayName("[File] 존재하지 않는 ID로 바이너리 컨텐트를 찾으면 Optional.empty()를 반환해야 한다")
  void findByIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when
    Optional<BinaryContent> foundContent = binaryContentRepository.findById(nonExistingId);

    // then
    assertThat(foundContent).isEmpty();
  }

  @Test
  @DisplayName("[File] 사용자 ID로 바이너리 컨텐트를 찾을 수 있어야 한다")
  void findByUserIdShouldReturnBinaryContentIfExists() throws IOException {
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
  @DisplayName("[File] 존재하지 않는 사용자 ID로 바이너리 컨텐트를 찾으면 Optional.empty()를 반환해야 한다")
  void findByUserIdShouldReturnEmptyOptionalIfNotFound() {
    // given
    UUID nonExistingUserId = UUID.randomUUID();

    // when
    Optional<BinaryContent> foundContent = binaryContentRepository.findByUserId(nonExistingUserId);

    // then
    assertThat(foundContent).isEmpty();
  }

  @Test
  @DisplayName("[File] 메시지 ID로 바이너리 컨텐트를 찾을 수 있어야 한다 (첨부파일 등록 후 메시지에 연결)")
  void findByMessageIdShouldReturnBinaryContentIfExists() throws IOException {
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
  @DisplayName("[File] 존재하지 않는 메시지 ID로 바이너리 컨텐트를 찾으면 빈 리스트를 반환해야 한다")
  void findByMessageIdShouldReturnEmptyListIfNotFound() {
    // given
    UUID nonExistingMessageId = UUID.randomUUID();

    // when
    List<BinaryContent> foundContent = binaryContentRepository.findAllByMessageId(
        nonExistingMessageId);

    // then
    assertThat(foundContent).isEmpty();
  }

  @Test
  @DisplayName("[File] 바이너리 컨텐트를 저장해야 한다")
  void saveShouldPersistBinaryContent() throws IOException {
    // given
    UUID contentId = UUID.randomUUID();
    BinaryContent contentToSave = BinaryContentFixture.createValidMessageAttachment();

    // when
    BinaryContent savedContent = binaryContentRepository.save(contentToSave);
    Optional<BinaryContent> loadedContent = binaryContentRepository.findById(savedContent.getId());

    // then
    assertThat(loadedContent).isPresent();
    assertThat(loadedContent.get().getId()).isEqualTo(savedContent.getId());
    assertThat(loadedContent.get().getFileName()).isEqualTo(contentToSave.getFileName());
    assertThat(loadedContent.get().getMimeType()).isEqualTo(contentToSave.getMimeType());
    assertThat(loadedContent.get().getContentType()).isEqualTo(contentToSave.getContentType());
    assertThat(loadedContent.get().getMessageId()).isEqualTo(contentToSave.getMessageId());
  }

  @Test
  @DisplayName("[File] ID로 바이너리 컨텐트를 삭제해야 한다")
  void deleteShouldRemoveBinaryContentIfExists() throws IOException {
    // given
    UUID contentId = UUID.randomUUID();
    BinaryContent savedContent = binaryContentRepository.save(
        BinaryContentFixture.createValidMessageAttachment());

    // when
    binaryContentRepository.delete(savedContent.getId());

    // then
    Optional<BinaryContent> deletedContent = binaryContentRepository.findById(savedContent.getId());
    assertThat(deletedContent).isEmpty();
  }

  @Test
  @DisplayName("[File] 존재하지 않는 ID로 삭제를 시도해도 예외가 발생하지 않아야 한다")
  void deleteShouldNotThrowExceptionIfNotFound() {
    // given
    UUID nonExistingId = UUID.randomUUID();

    // when & then
    // deleteById는 void 형식이므로 예외가 발생하지 않는지만 확인
    binaryContentRepository.delete(nonExistingId);
    assertTrue(true, "예외가 발생하지 않았음");
  }
}