package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.FileDeleteFailedException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContent create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.info("바이너리 콘텐츠 생성 요청 - 파일명: {}, 크기: {} bytes, 타입: {}",
        fileName, bytes.length, contentType);

    // 1. 메타정보만으로 BinaryContent 엔티티 생성 및 저장
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType);

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
    log.debug("바이너리 콘텐츠 메타데이터 저장 완료 - ID: {}", savedBinaryContent.getId());

    try {
      // 2. 실제 바이너리 데이터는 별도 저장소에 저장
      binaryContentStorage.put(savedBinaryContent.getId(), bytes);
      log.info("바이너리 콘텐츠 저장 완료 - ID: {}, 파일명: {}, 크기: {} bytes",
          savedBinaryContent.getId(), fileName, bytes.length);
    } catch (Exception e) {
      log.error("바이너리 콘텐츠 저장 실패 - ID: {}, 파일명: {}, 오류: {}",
          savedBinaryContent.getId(), fileName, e.getMessage());
      // 저장 실패 시 메타데이터도 롤백
      binaryContentRepository.delete(savedBinaryContent);
      throw FileUploadFailedException.withFilenameAndCause(fileName, e);
    }

    return savedBinaryContent;
  }

  @Override
  public BinaryContent createFromOptional(Optional<BinaryContentCreateRequest> optionalRequest) {
    if (optionalRequest.isPresent()) {
      log.debug("Optional 바이너리 콘텐츠 생성 요청");
      return create(optionalRequest.get());
    } else {
      log.debug("Optional 바이너리 콘텐츠 생성 요청 - 요청 없음");
      return null;
    }
  }

  @Override
  public List<BinaryContent> createAll(List<BinaryContentCreateRequest> requests) {
    log.info("다중 바이너리 콘텐츠 생성 요청 - 개수: {}", requests.size());

    List<BinaryContent> results = requests.stream()
        .map(this::create)
        .collect(Collectors.toList());

    log.info("다중 바이너리 콘텐츠 생성 완료 - 개수: {}", results.size());
    return results;
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent find(UUID binaryContentId) {
    log.debug("바이너리 콘텐츠 조회 요청 - ID: {}", binaryContentId);

    return binaryContentRepository.findById(binaryContentId)
        .map(content -> {
          log.debug("바이너리 콘텐츠 조회 완료 - ID: {}, 파일명: {}",
              content.getId(), content.getFileName());
          return content;
        })
        .orElseThrow(() -> {
          log.error("바이너리 콘텐츠 조회 실패 - 존재하지 않는 ID: {}", binaryContentId);
          return BinaryContentNotFoundException.withBinaryContentId(binaryContentId);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("다중 바이너리 콘텐츠 조회 요청 - ID 개수: {}", binaryContentIds.size());

    List<BinaryContent> results = binaryContentRepository.findAllByIdIn(binaryContentIds);
    log.debug("다중 바이너리 콘텐츠 조회 완료 - 요청: {}, 조회: {}",
        binaryContentIds.size(), results.size());

    return results;
  }

  @Override
  public void delete(UUID binaryContentId) {
    log.info("바이너리 콘텐츠 삭제 요청 - ID: {}", binaryContentId);

    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.error("바이너리 콘텐츠 삭제 실패 - 존재하지 않는 ID: {}", binaryContentId);
      throw BinaryContentNotFoundException.withBinaryContentId(binaryContentId);
    }

    try {
      // 실제 파일 삭제는 별도 처리가 필요할 수 있음 (현재는 메타데이터만 삭제)
      binaryContentRepository.deleteById(binaryContentId);
      log.info("바이너리 콘텐츠 삭제 완료 - ID: {}", binaryContentId);
    } catch (Exception e) {
      log.error("바이너리 콘텐츠 삭제 실패 - ID: {}, 오류: {}", binaryContentId, e.getMessage());
      throw FileDeleteFailedException.withIdAndCause(binaryContentId, e);
    }
  }
}
