package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @Transactional
  public BinaryContent create(@Valid BinaryContentCreateRequest binaryContentCreateRequest) {
    String fileName = binaryContentCreateRequest.fileName();
    byte[] bytes = binaryContentCreateRequest.bytes();
    String contentType = binaryContentCreateRequest.contentType();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    log.info("파일 entity 생성: {}", binaryContent);

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
    log.info("파일 메타데이터 저장 완료 - id: {}", savedBinaryContent.getId());
    binaryContentStorage.put(savedBinaryContent.getId(), bytes);

    return savedBinaryContent;
  }

  @Override
  public BinaryContentDto find(@NotNull UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> {
          log.error("파일 조회 실패 - binaryContentid={}", binaryContentId);
          return new NoSuchElementException("유효하지 않은 BinaryContent id (id=" + binaryContentId + ")");
        });

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(@NotNull List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);
    log.info("총 {}개의 BinaryContent 조회", binaryContents.size());

    return binaryContents.stream()
        .map(binaryContentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(@NotNull UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> {
          log.error("파일 조회 실패 - binaryContentId={}", binaryContentId);
          return new NoSuchElementException("유효하지 않은 BinaryContent id (id=" + binaryContentId + ")");
        });

    binaryContentRepository.delete(binaryContent);
  }

  @Override
  public InputStream getRawData(@NotNull UUID binaryContentId) {
    binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> {
          log.error("파일 조회 실패 - binaryContentId={}", binaryContentId);
          return new NoSuchElementException("유효하지 않은 BinaryContent id (id=" + binaryContentId + ")");
        });
    return binaryContentStorage.get(binaryContentId);
  }

  @Override
  public ResponseEntity<?> download(@NotNull BinaryContentDto dto) {
    return binaryContentStorage.download(dto);
  }
}