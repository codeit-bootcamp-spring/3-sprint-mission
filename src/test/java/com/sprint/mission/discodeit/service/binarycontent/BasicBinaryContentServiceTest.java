package com.sprint.mission.discodeit.service.binarycontent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  @Nested
  class Create {

    @Test
    void BinaryContent를_생성한다() {
      // given
      byte[] mockBytes = BinaryContentFixture.getDefaultData();

      BinaryContent expected = BinaryContent.create(
          BinaryContentFixture.getDefaultFileName(),
          (long) mockBytes.length,
          BinaryContentFixture.getDefaultMimeType()
      );
      expected.assignIdForTest(UUID.randomUUID());

      when(binaryContentRepository.save(any())).thenReturn(expected);

      var request = BinaryContentFixture.createValidData();

      // when
      BinaryContentResponse result = binaryContentService.create(request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.fileName()).isEqualTo(expected.getFileName());
      assertThat(result.contentType()).isEqualTo(expected.getContentType());
      assertThat(result.size()).isEqualTo(expected.getSize());

      verify(binaryContentRepository).save(any());
      verify(binaryContentStorage).put(any(UUID.class), eq(mockBytes));
    }
  }

  @Nested
  class Read {

    @Test
    void BinaryContent를_ID로_조회한다() {
      UUID id = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValid();

      when(binaryContentRepository.findById(id)).thenReturn(Optional.of(content));

      BinaryContentResponse found = binaryContentService.find(id);

      assertThat(found).isNotNull();
      assertThat(found.fileName()).isEqualTo(content.getFileName());
      verify(binaryContentRepository).findById(id);
    }
  }

  @Nested
  class Delete {

    @Test
    void BinaryContent를_ID로_삭제한다() {
      UUID id = UUID.randomUUID();

      binaryContentService.delete(id);

      verify(binaryContentRepository).deleteById(id);
    }
  }

  @Nested
  class FindAllByIdIn {

    @Test
    void 여러_ID로_BinaryContent_목록을_조회한다() {
      BinaryContent content1 = BinaryContentFixture.createValidWithId();
      BinaryContent content2 = BinaryContentFixture.createValidWithId();

      UUID id1 = content1.getId();
      UUID id2 = content2.getId();

      when(binaryContentRepository.findAllById(List.of(id1, id2)))
          .thenReturn(List.of(content1, content2));

      List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

      assertThat(result).hasSize(2);
      assertThat(result)
          .extracting(BinaryContentResponse::id)
          .containsExactlyInAnyOrder(content1.getId(), content2.getId());

      verify(binaryContentRepository).findAllById(List.of(id1, id2));
    }
  }
}
