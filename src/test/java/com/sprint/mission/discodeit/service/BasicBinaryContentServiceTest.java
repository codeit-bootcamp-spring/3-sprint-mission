package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("컨텐트 생성")
  class Create {

    @Test
    @DisplayName("프로필 이미지를 성공적으로 생성해야 한다")
    void createProfileImage_success() {
      UUID userId = UUID.randomUUID();
      BinaryContent expected = BinaryContentFixture.createValidProfileImage(userId);

      when(binaryContentRepository.save(any())).thenReturn(expected);

      var request = new BinaryContentCreateRequest(
          expected.getBytes(),
          expected.getFileName(),
          expected.getMimeType(),
          expected.getContentType(),
          expected.getUserId(),
          null
      );

      BinaryContent result = binaryContentService.create(request);

      assertThat(result).isNotNull();
      assertThat(result.getUserId()).isEqualTo(userId);
      assertThat(result.getContentType()).isEqualTo(BinaryContent.ContentType.PROFILE_IMAGE);
    }
  }

  @Nested
  @DisplayName("컨텐트 조회")
  class Read {

    @Test
    @DisplayName("BinaryContent를 ID로 조회한다")
    void find_success() {
      UUID id = UUID.randomUUID();
      BinaryContent content = BinaryContentFixture.createValidProfileImage(UUID.randomUUID());

      when(binaryContentRepository.findById(id)).thenReturn(Optional.of(content));

      BinaryContent found = binaryContentService.find(id);

      assertThat(found).isNotNull();
      assertThat(found.getFileName()).isEqualTo(content.getFileName());
    }
  }

  @Nested
  @DisplayName("컨텐트 삭제")
  class Delete {

    @Test
    @DisplayName("BinaryContent를 ID로 삭제한다")
    void delete_success() {
      UUID id = UUID.randomUUID();
      // 현재 delete는 반환값이 없으므로 예외만 안 터지면 성공
      binaryContentService.delete(id);
    }
  }
}
