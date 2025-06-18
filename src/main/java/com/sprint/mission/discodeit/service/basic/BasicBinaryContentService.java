package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BinaryContentException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  public BinaryContentResponse create(BinaryContentData binaryContentData) {
    if (binaryContentData.bytes() == null || binaryContentData.fileName() == null
        || binaryContentData.contentType() == null) {
      throw BinaryContentException.invalidRequest();
    }

    BinaryContent binaryContent = BinaryContent.create(
        binaryContentData.fileName(),
        (long) binaryContentData.bytes().length,
        binaryContentData.contentType()
    );

    BinaryContent saved = binaryContentRepository.save(binaryContent);

    binaryContentStorage.put(saved.getId(), binaryContentData.bytes());

    return BinaryContentResponse.from(saved);
  }

  @Override
  public BinaryContentResponse find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(BinaryContentResponse::from)
        .orElseThrow(() -> BinaryContentException.notFound(binaryContentId));
  }

  @Override
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentRepository.findAllById(binaryContentIds);

    if (contents.size() != binaryContentIds.size()) {
      throw BinaryContentException.notFound(); // 일부 ID 누락 검증
    }

    return contents.stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  @Override
  public ResponseEntity<Resource> download(UUID binaryContentId) {
    log.info("파일 다운로드 요청: {}", binaryContentId);

    BinaryContent entity = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> BinaryContentException.notFound(binaryContentId));

    return binaryContentStorage.download(entity);
  }

  @Override
  public void delete(UUID id) {
    binaryContentRepository.deleteById(id);
  }
}
