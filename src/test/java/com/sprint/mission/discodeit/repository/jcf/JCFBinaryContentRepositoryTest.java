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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JCFBinaryContentRepositoryTest {

  private JCFBinaryContentRepository binaryContentRepository;

  @BeforeEach
  void setUp() {
    binaryContentRepository = new JCFBinaryContentRepository();
  }

  @Test
  @DisplayName("findById로 저장된 BinaryContent를 조회한다.")
  void findById_returnsSavedBinaryContent() {
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
  @DisplayName("findById로 존재하지 않는 BinaryContent를 조회하면 Optional.empty()를 반환한다.")
  void findById_returnsEmptyOptional_whenNotFound() {
    // given
    UUID id = UUID.randomUUID();

    // when
    Optional<BinaryContent> foundContent = binaryContentRepository.findById(id);

    // then
    assertTrue(foundContent.isEmpty());
  }

  @Test
  @DisplayName("save로 BinaryContent를 저장하고 ID가 할당된다.")
  void save_assignsIdAndReturnsBinaryContent() {
    // given
    BinaryContent contentToSave = BinaryContentFixture.createValidProfileImage(UUID.randomUUID());

    // when
    BinaryContent savedContent = binaryContentRepository.save(contentToSave);

    // then
    assertNotNull(savedContent.getId());
    assertEquals(contentToSave.getData(), savedContent.getData());
    assertEquals(contentToSave.getFileName(), savedContent.getFileName());
    assertEquals(contentToSave.getMimeType(), savedContent.getMimeType());
  }

  @Test
  @DisplayName("deleteById로 저장된 BinaryContent를 삭제하고 조회하면 찾을 수 없다.")
  void deleteById_deletesBinaryContent() {
    // given
    BinaryContent contentToSave = BinaryContentFixture.createValidMessageAttachment();
    BinaryContent savedContent = binaryContentRepository.save(contentToSave);
    UUID idToDelete = savedContent.getId();

    // when
    binaryContentRepository.deleteById(idToDelete);
    Optional<BinaryContent> foundContent = binaryContentRepository.findById(idToDelete);

    // then
    assertTrue(foundContent.isEmpty());
  }

  @Test
  @DisplayName("findByUserId로 저장된 BinaryContent를 조회한다.")
  void findByUserId_returnsSavedBinaryContent() {
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