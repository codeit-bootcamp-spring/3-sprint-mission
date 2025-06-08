package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;


  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest binaryContentCreateRequest) {
    String fileName = binaryContentCreateRequest.fileName();
    byte[] bytes = binaryContentCreateRequest.bytes();
    String contentType = binaryContentCreateRequest.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(savedBinaryContent.getId(), binaryContentCreateRequest.bytes());

    return savedBinaryContent;
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);

    return binaryContents.stream()
        .map(binaryContentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    BinaryContent entity = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException(
            "BinaryContent with id " + binaryContentId + " not found"));
    binaryContentRepository.delete(entity);
  }

  @Override
  public InputStream getRawData(UUID binaryContentId) {
    binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + " not found"));
    return binaryContentStorage.get(binaryContentId);
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    return binaryContentStorage.download(dto);
  }
}
