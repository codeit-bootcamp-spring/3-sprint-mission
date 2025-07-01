package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileDeleteFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadFailedException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.testutil.TestConstants;
import com.sprint.mission.discodeit.testutil.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicBinaryContentService 단위 테스트")
class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicBinaryContentService basicBinaryContentService;

  // === create 메서드 테스트 ===

  @Test
  @DisplayName("바이너리 콘텐츠 생성 성공")
  void create_Success() {
    // given
    BinaryContentCreateRequest request = TestDataBuilder.createBinaryContentCreateRequest();

    // ID가 설정된 BinaryContent 엔티티 생성
    BinaryContent savedBinaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType());
    UUID testId = UUID.randomUUID();
    ReflectionTestUtils.setField(savedBinaryContent, "id", testId);

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);

    // when
    BinaryContent result = basicBinaryContentService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getFileName()).isEqualTo(request.fileName());
    assertThat(result.getSize()).isEqualTo((long) request.bytes().length);
    assertThat(result.getContentType()).isEqualTo(request.contentType());

    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(testId, request.bytes());
  }

  @Test
  @DisplayName("바이너리 콘텐츠 생성 실패 - 스토리지 저장 실패")
  void create_Failure_StorageException() {
    // given
    BinaryContentCreateRequest request = TestDataBuilder.createBinaryContentCreateRequest();

    // ID가 설정된 BinaryContent 엔티티 생성
    BinaryContent savedBinaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType());
    UUID testId = UUID.randomUUID();
    ReflectionTestUtils.setField(savedBinaryContent, "id", testId);

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);
    willThrow(new RuntimeException("스토리지 오류"))
        .given(binaryContentStorage).put(testId, request.bytes());

    // when & then
    assertThatThrownBy(() -> basicBinaryContentService.create(request))
        .isInstanceOf(FileUploadFailedException.class);

    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentRepository).should().delete(savedBinaryContent);
    then(binaryContentStorage).should().put(testId, request.bytes());
  }

  // === find 메서드 테스트 ===

  @Test
  @DisplayName("바이너리 콘텐츠 조회 성공")
  void find_Success() {
    // given
    UUID binaryContentId = UUID.randomUUID();
    BinaryContent expectedBinaryContent = new BinaryContent(
        TestConstants.TEST_FILE_NAME,
        TestConstants.TEST_FILE_SIZE,
        TestConstants.TEST_CONTENT_TYPE);

    given(binaryContentRepository.findById(binaryContentId))
        .willReturn(Optional.of(expectedBinaryContent));

    // when
    BinaryContent result = basicBinaryContentService.find(binaryContentId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getFileName()).isEqualTo(TestConstants.TEST_FILE_NAME);
    assertThat(result.getSize()).isEqualTo(TestConstants.TEST_FILE_SIZE);
    assertThat(result.getContentType()).isEqualTo(TestConstants.TEST_CONTENT_TYPE);

    then(binaryContentRepository).should().findById(binaryContentId);
  }

  @Test
  @DisplayName("바이너리 콘텐츠 조회 실패 - 존재하지 않는 ID")
  void find_Failure_NotFound() {
    // given
    UUID nonExistentId = TestConstants.NON_EXISTENT_USER_ID;

    given(binaryContentRepository.findById(nonExistentId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicBinaryContentService.find(nonExistentId))
        .isInstanceOf(BinaryContentNotFoundException.class);

    then(binaryContentRepository).should().findById(nonExistentId);
  }

  // === delete 메서드 테스트 ===

  @Test
  @DisplayName("바이너리 콘텐츠 삭제 성공")
  void delete_Success() {
    // given
    UUID binaryContentId = UUID.randomUUID();

    given(binaryContentRepository.existsById(binaryContentId)).willReturn(true);

    // when
    basicBinaryContentService.delete(binaryContentId);

    // then
    then(binaryContentRepository).should().existsById(binaryContentId);
    then(binaryContentRepository).should().deleteById(binaryContentId);
  }

  @Test
  @DisplayName("바이너리 콘텐츠 삭제 실패 - 존재하지 않는 ID")
  void delete_Failure_NotFound() {
    // given
    UUID nonExistentId = TestConstants.NON_EXISTENT_USER_ID;

    given(binaryContentRepository.existsById(nonExistentId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicBinaryContentService.delete(nonExistentId))
        .isInstanceOf(BinaryContentNotFoundException.class);

    then(binaryContentRepository).should().existsById(nonExistentId);
    then(binaryContentRepository).should(never()).deleteById(any());
  }

  @Test
  @DisplayName("바이너리 콘텐츠 삭제 실패 - 데이터베이스 오류")
  void delete_Failure_DatabaseException() {
    // given
    UUID binaryContentId = UUID.randomUUID();

    given(binaryContentRepository.existsById(binaryContentId)).willReturn(true);
    willThrow(new RuntimeException("데이터베이스 오류")).given(binaryContentRepository)
        .deleteById(binaryContentId);

    // when & then
    assertThatThrownBy(() -> basicBinaryContentService.delete(binaryContentId))
        .isInstanceOf(FileDeleteFailedException.class);

    then(binaryContentRepository).should().existsById(binaryContentId);
    then(binaryContentRepository).should().deleteById(binaryContentId);
  }
}
