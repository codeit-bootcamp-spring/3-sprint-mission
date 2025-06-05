package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.vo.BinaryContentData;
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
  private BinaryContentRepository binaryContentRepository;

  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  @Nested
  class Create {

    @Test
    void BinaryContent를_생성한다() {
      BinaryContent expected = BinaryContentFixture.createValid();

      when(binaryContentRepository.save(any())).thenReturn(expected);

      var request = new BinaryContentData(
          expected.getFileName(),
          expected.getContentType(),
          expected.getBytes()
      );

      BinaryContent result = binaryContentService.create(request);

      assertThat(result).isNotNull();
      assertThat(result.getFileName()).isEqualTo(expected.getFileName());
      assertThat(result.getContentType()).isEqualTo(expected.getContentType());
      assertThat(result.getBytes()).isEqualTo(expected.getBytes());
      verify(binaryContentRepository).save(any());
    }
  }

  @Nested
  class Read {

    @Test
    void BinaryContent를_ID로_조회한다() {
      UUID id = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValid();

      when(binaryContentRepository.findById(id)).thenReturn(Optional.of(content));

      BinaryContent found = binaryContentService.find(id);

      assertThat(found).isNotNull();
      assertThat(found.getFileName()).isEqualTo(content.getFileName());
      verify(binaryContentRepository).findById(id);
    }
  }

  @Nested
  class Delete {

    @Test
    void BinaryContent를_ID로_삭제한다() {
      UUID id = UUID.randomUUID();

      binaryContentService.delete(id);

      verify(binaryContentRepository).delete(id);
    }
  }

  @Nested
  class FindAllByIdIn {

    @Test
    void 여러_ID로_BinaryContent_목록을_조회한다() {
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();

      BinaryContent content1 = BinaryContentFixture.createValid();
      BinaryContent content2 = BinaryContentFixture.createValid();

      when(binaryContentRepository.findById(id1)).thenReturn(Optional.of(content1));
      when(binaryContentRepository.findById(id2)).thenReturn(Optional.of(content2));

      List<BinaryContent> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

      assertThat(result).hasSize(2);
      assertThat(result).contains(content1, content2);
      verify(binaryContentRepository).findById(id1);
      verify(binaryContentRepository).findById(id2);
    }
  }
}
