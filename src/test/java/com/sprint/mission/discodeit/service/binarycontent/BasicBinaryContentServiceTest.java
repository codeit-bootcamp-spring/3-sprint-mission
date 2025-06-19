package com.sprint.mission.discodeit.service.binarycontent;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
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
          BinaryContentFixture.getDefaultMimeType());
      expected.assignIdForTest(UUID.randomUUID());

      given(binaryContentRepository.save(any())).willReturn(expected);

      var request = BinaryContentFixture.createValidData();

      // when
      BinaryContentResponse result = binaryContentService.create(request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.fileName()).isEqualTo(expected.getFileName());
      assertThat(result.contentType()).isEqualTo(expected.getContentType());
      assertThat(result.size()).isEqualTo(expected.getSize());

      then(binaryContentRepository).should().save(any());
      then(binaryContentStorage).should().put(any(UUID.class), eq(mockBytes));
    }
  }

  @Nested
  class Read {

    @Test
    void BinaryContent를_ID로_조회한다() {
      UUID id = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValid();

      given(binaryContentRepository.findById(id)).willReturn(Optional.of(content));

      BinaryContentResponse found = binaryContentService.find(id);

      assertThat(found).isNotNull();
      assertThat(found.fileName()).isEqualTo(content.getFileName());
      then(binaryContentRepository).should().findById(id);
    }
  }

  @Nested
  class Delete {

    @Test
    void BinaryContent를_ID로_삭제한다() {
      UUID id = UUID.randomUUID();

      binaryContentService.delete(id);

      then(binaryContentRepository).should().deleteById(id);
    }
  }

  @Nested
  class FindAllByIdIn {

    @Test
    void 여러_ID로_BinaryContent_목록을_조회한다() {
      BinaryContent content1 = BinaryContentFixture.createValid();
      BinaryContent content2 = BinaryContentFixture.createValid();
      // save() mock을 통해 id가 할당된 객체를 반환하도록 설정
      BinaryContent saved1 = BinaryContent.create(content1.getFileName(), content1.getSize(),
          content1.getContentType());
      BinaryContent saved2 = BinaryContent.create(content2.getFileName(), content2.getSize(),
          content2.getContentType());
      saved1.assignIdForTest(UUID.randomUUID());
      saved2.assignIdForTest(UUID.randomUUID());
      UUID id1 = saved1.getId();
      UUID id2 = saved2.getId();

      given(binaryContentRepository.findAllById(List.of(id1, id2)))
          .willReturn(List.of(saved1, saved2));

      List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(List.of(id1, id2));

      assertThat(result).hasSize(2);
      assertThat(result)
          .extracting(BinaryContentResponse::id)
          .containsExactlyInAnyOrder(id1, id2);

      then(binaryContentRepository).should().findAllById(List.of(id1, id2));
    }
  }
}
