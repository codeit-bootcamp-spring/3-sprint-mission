package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    log.debug("파일 객체 생성 시작 request={}", request);
    BinaryContent binaryContent =
        BinaryContent.builder()
            .fileName(fileName)
            .contentType(contentType)
            .size((long) bytes.length)
            .build();
    log.debug("파일 객체 생성 완료 binaryContentId={}", binaryContent.getId());

    log.debug("[binaryContentRepository] 파일 저장 시작 binaryContentId={}", binaryContent.getId());
    binaryContentRepository.save(binaryContent);
    log.debug("[binaryContentRepository] 파일 저장 완료 binaryContentId={}", binaryContent.getId());

    log.debug("[binaryContentStorage] 파일 저장 시작 binaryContentId={}", binaryContent.getId());
    binaryContentStorage.put(binaryContent.getId(), bytes);
    log.debug("[binaryContentStorage] 파일 저장 완료 binaryContentId={}", binaryContent.getId());

    return binaryContentMapper.toDto(binaryContent);

  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!binaryContentRepository.existsById(id)) {
      log.warn("해당 파일이 존재하지 않습니다. binaryContentId={}", id);
      throw new BinaryContentNotFoundException(id);
    }

    log.debug("[binaryContentRepository] 파일 삭제 시작 binaryContentId={}", id);
    binaryContentRepository.deleteById(id);
    log.debug("[binaryContentRepository] 파일 삭제 완료 binaryContentId={}", id);
  }
}
