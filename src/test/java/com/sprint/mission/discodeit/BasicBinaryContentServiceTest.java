package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicBinaryContentServiceTest {

  private BinaryContentRepository binaryContentRepository;
  private BasicBinaryContentService binaryContentService;

  @BeforeEach
  void setUp() {
    binaryContentRepository = mock(BinaryContentRepository.class);
    binaryContentService = new BasicBinaryContentService(binaryContentRepository);
  }

  @Test
  void create_shouldSaveBinaryContent() {
    // given
    byte[] data = "hello".getBytes();
    BinaryContentCreateRequest request = new BinaryContentCreateRequest("file.txt", "text/plain",
        data);
    BinaryContent expected = new BinaryContent("file.txt", (long) data.length, "text/plain", data);

    when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(expected);

    // when
    BinaryContent result = binaryContentService.create(request);

    // then
    assertEquals("file.txt", result.getFilename());
    assertEquals(data.length, result.getSize());
    assertEquals("text/plain", result.getContentType());
    assertArrayEquals(data, result.getData());
  }

  @Test
  void find_shouldReturnBinaryContentWhenExists() {
    UUID id = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("img.png", 100L, "image/png", new byte[100]);
    when(binaryContentRepository.findById(id)).thenReturn(Optional.of(binaryContent));

    Optional<BinaryContent> result = binaryContentService.find(id);

    assertTrue(result.isPresent());
    assertEquals("img.png", result.get().getFilename());
  }

  @Test
  void find_shouldReturnEmptyWhenNotExists() {
    UUID id = UUID.randomUUID();
    when(binaryContentRepository.findById(id)).thenReturn(Optional.empty());

    Optional<BinaryContent> result = binaryContentService.find(id);

    assertFalse(result.isPresent());
  }

  @Test
  void findAllByIdIn_shouldReturnList() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    List<UUID> ids = List.of(id1, id2);
    List<BinaryContent> contents = List.of(
        new BinaryContent("a.txt", 10L, "text/plain", new byte[10]),
        new BinaryContent("b.txt", 20L, "text/plain", new byte[20])
    );

    when(binaryContentRepository.findAllByIdIn(ids)).thenReturn(contents);

    List<BinaryContent> result = binaryContentService.findAllByIdIn(ids);

    assertEquals(2, result.size());
  }

  @Test
  void delete_shouldCallRepository() {
    UUID id = UUID.randomUUID();

    binaryContentService.delete(id);

    verify(binaryContentRepository, times(1)).deleteById(id);
  }
}

