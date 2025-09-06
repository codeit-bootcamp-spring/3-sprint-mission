package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.InvalidBinaryContentRequestException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final com.sprint.mission.discodeit.service.SseService sseService;

  @Override
  public BinaryContentResponse create(BinaryContentData binaryContentData) {
    if (binaryContentData.bytes() == null || binaryContentData.fileName() == null
        || binaryContentData.contentType() == null) {
      throw new InvalidBinaryContentRequestException();
    }

    BinaryContent binaryContent = BinaryContent.create(
        binaryContentData.fileName(),
        (long) binaryContentData.bytes().length,
        binaryContentData.contentType());

    BinaryContent saved = binaryContentRepository.save(binaryContent);

    applicationEventPublisher.publishEvent(
        new BinaryContentCreatedEvent(saved.getId(), binaryContentData.bytes()));

    return BinaryContentResponse.from(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentResponse find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(BinaryContentResponse::from)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId.toString()));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentRepository.findAllById(binaryContentIds);

    if (contents.size() != binaryContentIds.size()) {
      throw new BinaryContentNotFoundException();
    }

    return contents.stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ResponseEntity<Resource> download(UUID binaryContentId) {
    log.info("파일 다운로드 요청: {}", binaryContentId);

    BinaryContent entity = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId.toString()));

    return binaryContentStorage.download(entity);
  }

  @Override
  public BinaryContentResponse updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent entity = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId.toString()));

    entity.updateStatus(status);
    BinaryContent updated = binaryContentRepository.save(entity);
    BinaryContentResponse response = BinaryContentResponse.from(updated);

    // TODO: 파일 소유자 ID 추출
    UUID ownerId = null;
    if (ownerId != null) {
      sseService.send(
          List.of(ownerId),
          "binaryContents.updated",
          response
      );
    }

    return response;
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.deleteById(id);
  }
}
